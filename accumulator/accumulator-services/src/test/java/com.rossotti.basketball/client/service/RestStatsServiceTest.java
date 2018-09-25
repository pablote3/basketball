package com.rossotti.basketball.client.service;

import com.rossotti.basketball.client.dto.GameDTO;
import com.rossotti.basketball.client.dto.RosterDTO;
import com.rossotti.basketball.client.dto.StandingsDTO;
import com.rossotti.basketball.client.service.exception.FileException;
import com.rossotti.basketball.util.StreamConverter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestStatsServiceTest {

	@Mock
	private RestClientService restClientService;

	@Mock
	private Environment env;

	@Mock
	private FileService fileService;

	@InjectMocks
	private RestStatsService restStatsService;

	@Test
	public void retrieveBoxScore_IllegalStateException() {
		when(env.getRequiredProperty(anyString()))
			.thenThrow(new IllegalStateException());
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", false);
		Assert.assertTrue(game.isServerException());
	}

	@Test
	public void retrieveBoxScore_NotFound() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", false);
		Assert.assertTrue(game.isNotFound());
	}

	@Test
	public void retrieveBoxScore_Unauthorized() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", false);
		Assert.assertTrue(game.isNotFound());
	}

	@Test
	public void retrieveBoxScore_IOException() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>("test".getBytes(), HttpStatus.OK));
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", false);
		Assert.assertTrue(game.isServerException());
	}

	@Test
	public void retrieveBoxScore_Found_NoPersist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/gameClient.json")), HttpStatus.OK));
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", false);
		Assert.assertTrue(game.isFound());
	}

	@Test
	public void retrieveBoxScore_FileException_Persist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/gameClient.json")), HttpStatus.OK));
		when(fileService.fileStreamWriter(anyString(), any(byte[].class)))
			.thenThrow(new FileException("IO Exception"));
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", true);
		Assert.assertTrue(game.isServerException());
	}

	@Test
	public void retrieveBoxScore_Found_Persist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/gameClient.json")), HttpStatus.OK));
		when(fileService.fileStreamWriter(anyString(), any(byte[].class)))
			.thenReturn(true);
		GameDTO game = restStatsService.retrieveBoxScore("20160311-houston-rockets-at-boston-celtics", true);
		Assert.assertTrue(game.isFound());
	}

	@Test
	public void retrieveRoster_IllegalStateException() {
		when(env.getRequiredProperty(anyString()))
			.thenThrow(new IllegalStateException());
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", false, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isServerException());
	}

	@Test
	public void retrieveRoster_NotFound() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", false, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isNotFound());
	}

	@Test
	public void retrieveRoster_Unauthorized() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", false, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isNotFound());
	}

	@Test
	public void retrieveRoster_IOException() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>("test".getBytes(), HttpStatus.OK));
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", false, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isServerException());
	}

	@Test
	public void retrieveRoster_Found_NoPersist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/rosterClient.json")), HttpStatus.OK));
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", false, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isFound());
	}

	@Test
	public void retrieveRoster_FileException_Persist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/rosterClient.json")), HttpStatus.OK));
		when(fileService.fileStreamWriter(anyString(), any(byte[].class)))
			.thenThrow(new FileException("IO Exception"));
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", true, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isServerException());
	}

	@Test
	public void retrieveRoster_Found_Persist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/rosterClient.json")), HttpStatus.OK));
		when(fileService.fileStreamWriter(anyString(), any(byte[].class)))
			.thenReturn(true);
		RosterDTO roster = restStatsService.retrieveRoster("houston-rockets", true, LocalDate.of(2016, 3, 11));
		Assert.assertTrue(roster.isFound());
	}
	@Test
	public void retrieveStandings_IllegalStateException() {
		when(env.getRequiredProperty(anyString()))
			.thenThrow(new IllegalStateException());
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", false);
		Assert.assertTrue(standings.isServerException());
	}

	@Test
	public void retrieveStandings_NotFound() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", false);
		Assert.assertTrue(standings.isNotFound());
	}

	@Test
	public void retrieveStandings_Unauthorized() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", false);
		Assert.assertTrue(standings.isNotFound());
	}

	@Test
	public void retrieveStandings_IOException() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>("test".getBytes(), HttpStatus.OK));
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", false);
		Assert.assertTrue(standings.isServerException());
	}

	@Test
	public void retrieveStandings_Found_NoPersist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/standingsClient.json")), HttpStatus.OK));
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", false);
		Assert.assertTrue(standings.isFound());
	}

	@Test
	public void retrieveStandings_FileException_Persist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/standingsClient.json")), HttpStatus.OK));
		when(fileService.fileStreamWriter(anyString(), any(byte[].class)))
			.thenThrow(new FileException("IO Exception"));
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", true);
		Assert.assertTrue(standings.isServerException());
	}

	@Test
	public void retrieveStandings_Found_Persist() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(restClientService.getJson(anyString()))
			.thenReturn(new ResponseEntity<>(StreamConverter.getBytes(getClass().getClassLoader().getResourceAsStream("mockClient/standingsClient.json")), HttpStatus.OK));
		when(fileService.fileStreamWriter(anyString(), any(byte[].class)))
			.thenReturn(true);
		StandingsDTO standings = restStatsService.retrieveStandings("20160311", true);
		Assert.assertTrue(standings.isFound());
	}
}