package com.rossotti.basketball.batch;

import com.rossotti.basketball.config.DatabaseConfig;
import com.rossotti.basketball.util.DateTimeConverter;
import com.rossotti.basketball.util.PropertyService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.time.LocalDate;

@Configuration
@EnableBatchProcessing
public class TeamBoxScoreConfig {

    private final PropertyService propertyService;

    private final DatabaseConfig databaseConfig;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public TeamBoxScoreConfig(PropertyService propertyService, DatabaseConfig databaseConfig, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.propertyService = propertyService;
        this.databaseConfig = databaseConfig;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job aggregateTeamBoxScoreJob() {
        return jobBuilderFactory.get("aggregateTeamBoxScoreJob")
            .incrementer(new RunIdIncrementer())
            .flow(stepTeamBoxScore())
            .end()
            .build();
    }

    @Bean
    public Step stepTeamBoxScore() {
        return stepBuilderFactory.get("stepTeamBoxScore")
            .<TeamBoxScore, TeamBoxScore>chunk(20)
            .reader(reader())
            .processor(teamBoxScoreProcessor())
            .writer(writer())
            .build();
    }

    @Bean
    public TeamBoxScoreProcessor teamBoxScoreProcessor() {
        return new TeamBoxScoreProcessor();
    }

    @SuppressWarnings("unchecked")
    @Bean
    public JdbcCursorItemReader<TeamBoxScore> reader() {
        LocalDate fromDate = propertyService.getProperty_LocalDate("aggregator.fromDate");
        LocalDate toDate = propertyService.getProperty_LocalDate("aggregator.toDate");

        JdbcCursorItemReader<TeamBoxScore> reader = new JdbcCursorItemReader<>();
        String minDateTime = DateTimeConverter.getStringDateTime(DateTimeConverter.getLocalDateTimeMin(fromDate));
        String maxDateTime = DateTimeConverter.getStringDateTime(DateTimeConverter.getLocalDateTimeMax(toDate));
        String sql =
            "SELECT game.gameDateTime, game.seasonType, " +
                "team.abbr as teamAbbr, team.conference as teamConf, team.division as teamDiv, " +
                "bsTeam.location as teamLocation, bsTeam.result as teamResult, bsTeam.minutes as teamMinutes, bsTeam.daysOff as teamDaysOff, " +
                "bsTeam.points as teamPTS, bsTeam.assists as teamAST, bsTeam.turnovers as teamTOV, bsTeam.steals as teamSTL, bsTeam.blocks as teamBLK, bsTeam.personalFouls as teamPF, " +
                "bsTeam.fieldGoalAttempts as teamFGA, bsTeam.fieldGoalMade as teamFGM, bsTeam.threePointAttempts as team3PA, bsTeam.threePointMade as team3PM, " +
                "bsTeam.freeThrowAttempts as teamFTA, bsTeam.freeThrowMade as teamFTM, bsTeam.reboundsOffense as teamOREB, bsTeam.reboundsDefense as teamDREB, " +
                "bsTeam.pointsPeriod1 as teamPTQ1, bsTeam.pointsPeriod2 as teamPTQ2, bsTeam.pointsPeriod3 as teamPTQ3, bsTeam.pointsPeriod4 as teamPTQ4, " +
                "bsTeam.pointsPeriod5 as teamPTQ5, bsTeam.pointsPeriod6 as teamPTQ6, bsTeam.pointsPeriod7 as teamPTQ7, bsTeam.pointsPeriod8 as teamPTQ8, " +
                "oppt.abbr as opptAbbr, oppt.conference as opptConf, oppt.division as opptDiv, " +
                "bsOppt.location as opptLocation, bsOppt.result as opptResult, bsOppt.minutes as opptMinutes, bsOppt.daysOff as opptDaysOff, " +
                "bsOppt.points as opptPTS, bsOppt.assists as opptAST, bsOppt.turnovers as opptTOV, bsOppt.steals as opptSTL, bsOppt.blocks as opptBLK, bsOppt.personalFouls as opptPF, " +
                "bsOppt.fieldGoalAttempts as opptFGA, bsOppt.fieldGoalMade as opptFGM, bsOppt.threePointAttempts as oppt3PA, bsOppt.threePointMade as oppt3PM, " +
                "bsOppt.freeThrowAttempts as opptFTA, bsOppt.freeThrowMade as opptFTM, bsOppt.reboundsOffense as opptOREB, bsOppt.reboundsDefense as opptDREB, " +
                "bsOppt.pointsPeriod1 as opptPTQ1, bsOppt.pointsPeriod2 as opptPTQ2, bsOppt.pointsPeriod3 as opptPTQ3, bsOppt.pointsPeriod4 as opptPTQ4, " +
                "bsOppt.pointsPeriod5 as opptPTQ5, bsOppt.pointsPeriod6 as opptPTQ6, bsOppt.pointsPeriod7 as opptPTQ7, bsOppt.pointsPeriod8 as opptPTQ8 " +
            "FROM game AS game " +
            "INNER JOIN boxScore AS bsTeam ON game.id = bsTeam.gameId " +
            "INNER JOIN team AS team ON team.id = bsTeam.teamId " +
            "INNER JOIN boxScore AS bsOppt ON game.id = bsOppt.gameId " +
            "INNER JOIN team AS oppt ON oppt.id = bsOppt.teamId " +
            "WHERE game.gameDateTime BETWEEN '" + minDateTime + "' AND '" + maxDateTime + "' " +
            "AND game.status = 'Completed' " +
            "AND team.abbr <> oppt.abbr " +
            "ORDER BY game.gameDateTime asc";

        System.out.println("sql = " +sql);

        reader.setSql(sql);
        reader.setDataSource(databaseConfig.dataSourceAccumulate());
        reader.setRowMapper(new TeamBoxScoreMapper());
        return reader;
    }

    @Bean
    public ItemWriter<TeamBoxScore> writer() {
        String destination = propertyService.getProperty_String("writer.destination");
        System.out.println("made it to the writer for " + destination);
        if (destination.equals("database")) {
            return jdbcWriter();
        }
        else if (destination.equals("file")) {
            return fileWriter();
        }
        else {
            System.out.println("invalid property value for writer.destination");
            return null;
        }
    }

    public ItemWriter<TeamBoxScore> fileWriter() {
        FlatFileItemWriter<TeamBoxScore> flatFileItemWriter = new FlatFileItemWriter<>();
        String path = propertyService.getProperty_Path("writer.extract");
        if (path != null) {
            flatFileItemWriter.setResource(new FileSystemResource(new File(path + "/teamBoxScore_Extract.txt")));
            flatFileItemWriter.setShouldDeleteIfExists(true);
            BeanWrapperFieldExtractor<TeamBoxScore> fieldExtractor = new BeanWrapperFieldExtractor<>();
            String[] fields = new String[]{
                "gameDateTime", "seasonType",
                "teamAbbr", "teamConference", "teamDivision", "teamLocation", "teamResult", "teamMinutes", "teamDaysOff", "teamPoints", "teamAssists",
                "teamTurnovers", "teamSteals", "teamBlocks", "teamPersonalFouls", "teamFieldGoalAttempts", "teamFieldGoalMade", "teamFieldGoalPct",
                "teamTwoPointAttempts", "teamTwoPointMade", "teamTwoPointPct", "teamThreePointAttempts", "teamThreePointMade", "teamThreePointPct",
                "teamFreeThrowAttempts", "teamFreeThrowMade", "teamFreeThrowPct", "teamReboundsOffense", "teamReboundsDefense", "teamReboundsTotal",
                "teamPointsQ1", "teamPointsQ2", "teamPointsQ3", "teamPointsQ4", "teamPointsQ5", "teamPointsQ6", "teamPointsQ7", "teamPointsQ8", "teamTotalReboundPct",
                "teamAssistedFieldGoalPct", "teamTrueShootingPct", "teamEffectiveFieldGoalPct", "teamOffensiveReboundPct", "teamDefensiveReboundPct", "teamTurnoverPct",
                "teamStealPct", "teamBlockPct", "teamBlockRate", "teamPointsPerShot", "teamFloorImpactCounter", "teamFloorImpactCounterPer40", "teamOffensiveRating",
                "teamDefensiveRating", "teamEfficiencyDifferential", "teamPlayPct", "teamAssistRate", "teamAssistToTurnoverRatio", "teamStealToTurnoverRatio",

                "opptAbbr", "opptConference", "opptDivision", "opptLocation", "opptResult", "opptMinutes", "opptDaysOff", "opptPoints", "opptAssists",
                "opptTurnovers", "opptSteals", "opptBlocks", "opptPersonalFouls", "opptFieldGoalAttempts", "opptFieldGoalMade", "opptFieldGoalPct",
                "opptTwoPointAttempts", "opptTwoPointMade", "opptTwoPointPct", "opptThreePointAttempts", "opptThreePointMade", "opptThreePointPct",
                "opptFreeThrowAttempts", "opptFreeThrowMade", "opptFreeThrowPct", "opptReboundsOffense", "opptReboundsDefense", "opptReboundsTotal",
                "opptPointsQ1", "opptPointsQ2", "opptPointsQ3", "opptPointsQ4", "opptPointsQ5", "opptPointsQ6", "opptPointsQ7", "opptPointsQ8",
                "opptAssistedFieldGoalPct", "opptTrueShootingPct", "opptEffectiveFieldGoalPct", "opptOffensiveReboundPct", "opptDefensiveReboundPct", "opptTurnoverPct",
                "opptStealPct", "opptBlockPct", "opptBlockRate", "opptPointsPerShot", "opptFloorImpactCounter", "opptFloorImpactCounterPer40", "opptOffensiveRating",
                "opptDefensiveRating", "opptEfficiencyDifferential", "opptPlayPct", "opptAssistRate", "opptAssistToTurnoverRatio", "opptStealToTurnoverRatio",

                "possessions", "pace", "teamTrueShootingPct", "pythagoreanWinningPct_13_91", "pythagoreanWins_13_91", "pythagoreanLosses_13_91",
                "pythagoreanWinningPct_16_5", "pythagoreanWins_16_5", "pythagoreanLosses_16_5"
            };
            fieldExtractor.setNames(fields);
            DelimitedLineAggregator<TeamBoxScore> lineAggregator = new DelimitedLineAggregator<>();
            lineAggregator.setFieldExtractor(fieldExtractor);
            flatFileItemWriter.setLineAggregator(lineAggregator);
            return flatFileItemWriter;
        }
        else {
            return null;
        }
    }

    public ItemWriter<TeamBoxScore> jdbcWriter() {
        JdbcBatchItemWriter<TeamBoxScore> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(databaseConfig.dataSourceAggregate());
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        String sql =
            "INSERT INTO teamBoxScore " +
            "(" +
                "gameDateTime, seasonType, " +
                "teamAbbr, teamConference, teamDivision, teamLocation, teamResult, teamMinutes, teamDaysOff, teamPoints, teamAssists, " +
                "teamTurnovers, teamSteals, teamBlocks, teamPersonalFouls, teamFieldGoalAttempts, teamFieldGoalMade, teamFieldGoalPct, " +
                "teamTwoPointAttempts, teamTwoPointMade, teamTwoPointPct, teamThreePointAttempts, teamThreePointMade, teamThreePointPct, " +
                "teamFreeThrowAttempts, teamFreeThrowMade, teamFreeThrowPct, teamReboundsOffense, teamReboundsDefense, teamReboundsTotal, " +
                "teamPointsQ1, teamPointsQ2, teamPointsQ3, teamPointsQ4, teamPointsQ5, teamPointsQ6, teamPointsQ7, teamPointsQ8, " +
                "teamTrueShootingPct, teamEffectiveFieldGoalPct, teamOffensiveReboundPct, teamDefensiveReboundPct, teamTotalReboundPct, " +
                "teamAssistedFieldGoalPct, teamTurnoverPct, teamStealPct, teamBlockPct, teamBlockRate, teamPointsPerShot, teamFloorImpactCounter, " +
                "teamFloorImpactCounterPer40, teamOffensiveRating, teamDefensiveRating, teamEfficiencyDifferential, teamPlayPct, teamAssistRate, " +
                "teamAssistToTurnoverRatio, teamStealToTurnoverRatio, " +

                "opptAbbr, opptConference, opptDivision, opptLocation, opptResult, opptMinutes, opptDaysOff, opptPoints, opptAssists, " +
                "opptTurnovers, opptSteals, opptBlocks, opptPersonalFouls, opptFieldGoalAttempts, opptFieldGoalMade, opptFieldGoalPct, " +
                "opptTwoPointAttempts, opptTwoPointMade, opptTwoPointPct, opptThreePointAttempts, opptThreePointMade, opptThreePointPct, " +
                "opptFreeThrowAttempts, opptFreeThrowMade, opptFreeThrowPct, opptReboundsOffense, opptReboundsDefense, opptReboundsTotal, " +
                "opptPointsQ1, opptPointsQ2, opptPointsQ3, opptPointsQ4, opptPointsQ5, opptPointsQ6, opptPointsQ7, opptPointsQ8, " +
                "opptTrueShootingPct, opptEffectiveFieldGoalPct, opptOffensiveReboundPct, opptDefensiveReboundPct, opptTotalReboundPct, " +
                "opptAssistedFieldGoalPct, opptTurnoverPct, opptStealPct, opptBlockPct, opptBlockRate, opptPointsPerShot, opptFloorImpactCounter, " +
                "opptFloorImpactCounterPer40, opptOffensiveRating, opptDefensiveRating, opptEfficiencyDifferential, opptPlayPct, opptAssistRate, " +
                "opptAssistToTurnoverRatio, opptStealToTurnoverRatio, " +

                "possessions, pace, pythagoreanWinningPct_13_91, pythagoreanWins_13_91, pythagoreanLosses_13_91, pythagoreanWinningPct_16_5, " +
                "pythagoreanWins_16_5, pythagoreanLosses_16_5" +
            ") " +
            "VALUES " +
            "(" +
                ":gameDateTime, :seasonType, " +
                ":teamAbbr, :teamConference, :teamDivision, :teamLocation, :teamResult, :teamMinutes, :teamDaysOff, :teamPoints, :teamAssists, " +
                ":teamTurnovers, :teamSteals, :teamBlocks, :teamPersonalFouls, :teamFieldGoalAttempts, :teamFieldGoalMade, :teamFieldGoalPct, " +
                ":teamTwoPointAttempts, :teamTwoPointMade, :teamTwoPointPct, :teamThreePointAttempts, :teamThreePointMade, :teamThreePointPct, " +
                ":teamFreeThrowAttempts, :teamFreeThrowMade, :teamFreeThrowPct, :teamReboundsOffense, :teamReboundsDefense, :teamReboundsTotal, " +
                ":teamPointsQ1, :teamPointsQ2, :teamPointsQ3, :teamPointsQ4, :teamPointsQ5, :teamPointsQ6, :teamPointsQ7, :teamPointsQ8, " +
                ":teamTrueShootingPct, :teamEffectiveFieldGoalPct, :teamOffensiveReboundPct, :teamDefensiveReboundPct, :teamTotalReboundPct, " +
                ":teamAssistedFieldGoalPct, :teamTurnoverPct, :teamStealPct, :teamBlockPct, :teamBlockRate, :teamPointsPerShot, :teamFloorImpactCounter, " +
                ":teamFloorImpactCounterPer40, :teamOffensiveRating, :teamDefensiveRating, :teamEfficiencyDifferential, :teamPlayPct, :teamAssistRate, " +
                ":teamAssistToTurnoverRatio, :teamStealToTurnoverRatio, " +

                ":opptAbbr, :opptConference, :opptDivision, :opptLocation, :opptResult, :opptMinutes, :opptDaysOff, :opptPoints, :opptAssists, " +
                ":opptTurnovers, :opptSteals, :opptBlocks, :opptPersonalFouls, :opptFieldGoalAttempts, :opptFieldGoalMade, :opptFieldGoalPct, " +
                ":opptTwoPointAttempts, :opptTwoPointMade, :opptTwoPointPct, :opptThreePointAttempts, :opptThreePointMade, :opptThreePointPct, " +
                ":opptFreeThrowAttempts, :opptFreeThrowMade, :opptFreeThrowPct, :opptReboundsOffense, :opptReboundsDefense, :opptReboundsTotal, " +
                ":opptPointsQ1, :opptPointsQ2, :opptPointsQ3, :opptPointsQ4, :opptPointsQ5, :opptPointsQ6, :opptPointsQ7, :opptPointsQ8, " +
                ":opptTrueShootingPct, :opptEffectiveFieldGoalPct, :opptOffensiveReboundPct, :opptDefensiveReboundPct, :opptTotalReboundPct, " +
                ":opptAssistedFieldGoalPct, :opptTurnoverPct, :opptStealPct, :opptBlockPct, :opptBlockRate, :opptPointsPerShot, :opptFloorImpactCounter, " +
                ":opptFloorImpactCounterPer40, :opptOffensiveRating, :opptDefensiveRating, :opptEfficiencyDifferential, :opptPlayPct, :opptAssistRate, " +
                ":opptAssistToTurnoverRatio, :opptStealToTurnoverRatio, " +

                ":possessions, :pace, :pythagoreanWinningPct_13_91, :pythagoreanWins_13_91, :pythagoreanLosses_13_91, :pythagoreanWinningPct_16_5," +
                ":pythagoreanWins_16_5, :pythagoreanLosses_16_5" +
            ")";
        jdbcBatchItemWriter.setSql(sql);
        return jdbcBatchItemWriter;
    }
}