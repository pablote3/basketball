package com.rossotti.basketball.client.service;

import com.rossotti.basketball.client.dto.GameDTO;
import com.rossotti.basketball.client.dto.RosterDTO;
import com.rossotti.basketball.client.dto.StandingsDTO;
import com.rossotti.basketball.client.dto.StatusCodeDTO.StatusCode;
import com.rossotti.basketball.util.ThreadSleep;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.rossotti.basketball.config.SpringConfig.class)
public class RestClientServiceTest {

	@Autowired
	private RestStatsService restStatsService;

	@Autowired
	private RestClientService restClientService;

	@Ignore
	@Test
	public void retrieveRoster_200() {
		String event = "toronto-raptors";
		RosterDTO roster = restStatsService.retrieveRoster(event, false, LocalDate.of(2016, 12, 15));
		Assert.assertEquals(StatusCode.Found, roster.getStatusCode());
		Assert.assertEquals(15, roster.players.length);
	}

	@Ignore
	@Test
	public void retrieveStandings_200() {
		String event = "20141028";
		StandingsDTO standings = restStatsService.retrieveStandings(event, false);
		Assert.assertEquals(StatusCode.Found, standings.getStatusCode());
		Assert.assertEquals(30, standings.standing.length);
	}

	@Ignore
	@Test
	public void retrieveBoxScore_200() {
		String event = "20150415-utah-jazz-at-houston-rockets";
		GameDTO game = restStatsService.retrieveBoxScore(event, false);
		Assert.assertEquals(StatusCode.Found, game.getStatusCode());
		Assert.assertEquals(3, game.officials.length);
	}

	@Ignore
	@Test
	public void retrieveRoster_401() throws IllegalStateException {
        //bad access token
		String rosterUrl = "https://erikberg.com/nba/roster/toronto-raptors.json";
		int status = restClientService.getJson(rosterUrl).getStatusCode().value();
		Assert.assertEquals(401, status);
	}

	@Ignore
	@Test
	public void retrieveRoster_404() throws IllegalStateException {
		String badUrl = "https://erikberg.com/nba/roster/toronto-raps.json";
		int status = restClientService.getJson(badUrl).getStatusCode().value();
		Assert.assertEquals(404, status);
	}

	@Ignore
	@Test
	public void retrieveRoster_403() throws IllegalStateException {
		//could cause ban of IP
        //bad userAgent
		String rosterUrl = "https://erikberg.com/nba/roster/toronto-raptors.json";
		int status = restClientService.getJson(rosterUrl).getStatusCode().value();
		Assert.assertEquals(403, status);
	}

	@Ignore
	@Test
	public void retrieveRoster_429() throws IllegalStateException {
		//sending more than 6 requests in a minute is counted against account
		String rosterUrl = "https://erikberg.com/nba/roster/toronto-raptors.json";
		int status200 = restClientService.getJson(rosterUrl).getStatusCode().value();
		Assert.assertEquals(200, status200);
		ThreadSleep.sleep(1);
		int status429 = restClientService.getJson(rosterUrl).getStatusCode().value();
		Assert.assertEquals(429, status429);
	}
}