package com.rossotti.basketball.client.service;

import com.rossotti.basketball.client.dto.GameDTO;
import com.rossotti.basketball.client.dto.RosterDTO;
import com.rossotti.basketball.client.dto.StandingsDTO;
import com.rossotti.basketball.client.dto.StatusCodeDTO.StatusCode;
import com.rossotti.basketball.util.DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class FileStatsService {
	private final FileClientService fileClientService;
	private final Environment env;
	private final Logger logger = LoggerFactory.getLogger(FileStatsService.class);

	@Autowired
	public FileStatsService(FileClientService fileClientService, Environment env) {
		this.fileClientService = fileClientService;
		this.env = env;
	}

	public GameDTO retrieveBoxScore(String event) {
		GameDTO gameDTO = new GameDTO();
		try {
			String path = env.getRequiredProperty("xmlstats.fileBoxScore");
			gameDTO = (GameDTO)fileClientService.retrieveStats(path, event, gameDTO);
		}
		catch (IllegalStateException pe) {
			logger.info("property exception = " + pe);
			gameDTO.setStatusCode(StatusCode.ServerException);
		}
		return gameDTO;
	}

	public RosterDTO retrieveRoster(String event, LocalDate asOfDate) {
		RosterDTO rosterDTO = new RosterDTO();
		try {
			String path = env.getRequiredProperty("xmlstats.fileRoster");
			String dateEvent = event + "-" + DateTimeConverter.getStringDateNaked(asOfDate);
			rosterDTO = (RosterDTO)fileClientService.retrieveStats(path, dateEvent, rosterDTO);
		}
		catch (IllegalStateException pe) {
			logger.info("property exception = " + pe);
			rosterDTO.setStatusCode(StatusCode.ServerException);
		}
		return rosterDTO;
	}

	public StandingsDTO retrieveStandings(String event) {
		StandingsDTO standingsDTO = new StandingsDTO();
		try {
			String path = env.getRequiredProperty("xmlstats.fileStandings");
			standingsDTO = (StandingsDTO)fileClientService.retrieveStats(path, event, standingsDTO);
		}
		catch (IllegalStateException pe) {
			logger.info("property exception = " + pe);
			standingsDTO.setStatusCode(StatusCode.ServerException);
		}
		return standingsDTO;
	}
}