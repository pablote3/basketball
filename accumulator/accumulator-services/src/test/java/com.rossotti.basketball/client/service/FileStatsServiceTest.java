package com.rossotti.basketball.client.service;

import com.rossotti.basketball.client.dto.GameDTO;
import com.rossotti.basketball.client.dto.RosterDTO;
import com.rossotti.basketball.client.dto.StandingsDTO;
import com.rossotti.basketball.client.dto.StatusCodeDTO;
import com.rossotti.basketball.client.dto.StatusCodeDTO.StatusCode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileStatsServiceTest {

	@Mock
	private FileClientService fileClientService;

	@Mock
	private Environment env;

	@InjectMocks
	private FileStatsService fileStatsService;

	@Test
	public void retrieveBoxScore_IllegalStateException() {
		when(env.getRequiredProperty(anyString()))
				.thenThrow(new IllegalStateException());
		GameDTO game = fileStatsService.retrieveBoxScore("20150415-utah-jazz-at-houston-rockets");
		Assert.assertTrue(game.isServerException());
	}

	@Test
	public void retrieveBoxScore_NotFound() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new GameDTO(), StatusCode.NotFound));
		GameDTO game = fileStatsService.retrieveBoxScore("20150415-utah-jazz-at-houston-rockets");
		Assert.assertTrue(game.isNotFound());
	}

	@Test
	public void retrieveBoxScore_ClientException() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new GameDTO(), StatusCode.ClientException));
		GameDTO game = fileStatsService.retrieveBoxScore("20150415-utah-jazz-at-houston-rockets");
		Assert.assertTrue(game.isClientException());
	}

	@Test
	public void retrieveBoxScore_Found() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileBoxScore");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new GameDTO(), StatusCode.Found));
		GameDTO game = fileStatsService.retrieveBoxScore("20150415-utah-jazz-at-houston-rockets");
		Assert.assertTrue(game.isFound());
	}

	@Test
	public void retrieveRoster_IllegalStateException() {
		when(env.getRequiredProperty(anyString()))
			.thenThrow(new IllegalStateException());
		RosterDTO roster = fileStatsService.retrieveRoster("toronto-raptors", LocalDate.of(2015, 4, 15));
		Assert.assertTrue(roster.isServerException());
	}

	@Test
	public void retrieveRoster_NotFound() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new RosterDTO(), StatusCode.NotFound));
		RosterDTO roster = fileStatsService.retrieveRoster("toronto-raptors-20150415", LocalDate.of(2015, 4, 15));
		Assert.assertTrue(roster.isNotFound());
	}

	@Test
	public void retrieveRoster_ClientException() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new RosterDTO(), StatusCode.ClientException));
		RosterDTO roster = fileStatsService.retrieveRoster("toronto-raptors", LocalDate.of(2015, 4, 15));
		Assert.assertTrue(roster.isClientException());
	}

	@Test
	public void retrieveRoster_Found() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileRoster");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new RosterDTO(), StatusCode.Found));
		RosterDTO roster = fileStatsService.retrieveRoster("toronto-raptors", LocalDate.of(2015, 4, 15));
		Assert.assertTrue(roster.isFound());
	}

	@Test
	public void retrieveStandings_IllegalStateException() {
		when(env.getRequiredProperty(anyString()))
			.thenThrow(new IllegalStateException());
		StandingsDTO standings = fileStatsService.retrieveStandings("20141028");
		Assert.assertTrue(standings.isServerException());
	}

	@Test
	public void retrieveStandings_NotFound() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new StandingsDTO(), StatusCode.NotFound));
		StandingsDTO standings = fileStatsService.retrieveStandings("20141028");
		Assert.assertTrue(standings.isNotFound());
	}

	@Test
	public void retrieveStandings_ClientException() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new StandingsDTO(), StatusCode.ClientException));
		StandingsDTO standings = fileStatsService.retrieveStandings("20141028");
		Assert.assertTrue(standings.isClientException());
	}

	@Test
	public void retrieveStandings_Found() {
		when(env.getRequiredProperty(anyString()))
			.thenReturn("/home/pablote/pdrive/pwork/basketball-java/accumulator/tests/unit/fileStandings");
		when(fileClientService.retrieveStats(anyString(), anyString(), any()))
			.thenReturn(createMockStatsDTO(new StandingsDTO(), StatusCode.Found));
		StandingsDTO standings = fileStatsService.retrieveStandings("20141028");
		Assert.assertTrue(standings.isFound());
	}

	private StatusCodeDTO createMockStatsDTO(StatusCodeDTO statusCodeDTO, StatusCode statusCode) {
		statusCodeDTO.setStatusCode(statusCode);
		return statusCodeDTO;
	}
}