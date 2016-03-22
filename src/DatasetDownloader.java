/* 
 * Copyright (C) 2016  Yves Bastos
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class DatasetDownloader {
	private static BufferedReader bufferedReader;
	private String[] datasetsToDownload;
	private String[] dateRange;
	private boolean ignoreOffset;
	private static String token;
	
	/**
	 * 
	 * @param datasetsToDownload {"PRECIP_HLY", "NORMAL_MLY", "ANNUAL", "GHCND", "GHCNDMS", "NEXRAD2", "NEXRAD3", "NORMAL_ANN", "NORMAL_DLY", "NORMAL_HLY", "NORMAL_MLY", "PRECIP_15", "PRECIP_HLY"}
	 * @param dateRange First element: beginning. Second: final date.
	 * @throws IOException
	 * @throws UnirestException
	 */
	public DatasetDownloader(String token, String[] datasetsToDownload, String[] dateRange, boolean ignoreOffset) throws IOException, UnirestException {
		// Datasets: Annual Summaries, Daily Summaries, Monthly Summaries, Weather Radar (Level II), Weather Radar (Level III), Normals Annual/Seasonal, Normals Daily, Normals Hourly, Normals Monthly, Precipitation 15 Minute, Precipitation Hourly
		this.token = token;
		this.dateRange = dateRange; 
		this.datasetsToDownload = datasetsToDownload;
		this.ignoreOffset = ignoreOffset;
		
		beginDownload();
	}

	private void beginDownload() throws IOException, UnirestException {
		int requestsCount = 1; 

		for (String dataset: datasetsToDownload) {
			File file = new File("data/datasets/" + dataset);
			if (!file.exists())
			{
				file.mkdir();
			}
			int offset = getOffsetFromFile(dataset, ignoreOffset);  
			//TODO PROBLEM IF DOWNLOADING MORE THAN ONE DATASET: OFFSET AMBIGUOUS
			Boolean endOfFile = false;

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();

			//int recordsDownloaded = 0;
			System.out.println("*\nDataset being downloaded: " + dataset + ".");
			saveLog("\n\n\n" + dateFormat.format(date) + "\n\n*\nDataset being downloaded: " + dataset + ".");

			while ((requestsCount < 1000) || (endOfFile == false)) { 
				HttpResponse<JsonNode> data = getData("PRECIP_HLY", "&startdate=" + dateRange[0] + "&enddate=" + dateRange[1] + "&limit=1000&offset=" + Integer.toString(offset)); 
				JsonNode dataBatch = data.getBody();
				//recordsDownloaded+=dataBatch.getArray().length();

				//System.out.println(requestsCount + " requests");
				saveLog("\n" + requestsCount + " requests");

				if (dataBatch.toString().equals("{}")) {
					endOfFile = true;
					System.out.println("End of dataset.");
					saveLog("End of" + dataset + "dataset. ");
					saveSuccessfulPull(dataset, Integer.toString(requestsCount-1), new String(dateRange[0] + " to " + dateRange[1]));
					break;
				} else {
					saveClimateDataToFile(dataBatch.toString(), new String("data/datasets/" + dataset + "/" + dataset + ".txt"));
					requestsCount++;			
					offset+=1000;
					saveOffsetToFile(dataset, offset);
				}


				//Thread.sleep(1000);

			}
		}
	}

	/**
	 * Unirest library: interacts with the RestfulAPI
	 * @param datasetId
	 * @param parameters
	 * @return
	 * @throws UnirestException
	 */
	private static HttpResponse<JsonNode> getData(String datasetId, String parameters) throws UnirestException {
		HttpResponse<JsonNode> response = Unirest.get("http://www.ncdc.noaa.gov/cdo-web/api/v2/data?datasetid=" + datasetId + parameters)
				.header("token", token)
				.header("cache-control", "no-cache")
				.header("postman-token", "b6fdf8e7-4f99-4e38-01f6-3139bd0876fa")
				.asJson();
		return response;
	}

	/**
	 * 
	 * @param ignoreOffset
	 * @return
	 * @throws IOException
	 */
	private static int getOffsetFromFile(String dataset, boolean ignoreOffset) throws IOException {
		if (ignoreOffset) {
			saveOffsetToFile(dataset, 1);
			return 1; 
		}
		
		try {
			bufferedReader = new BufferedReader(new FileReader("data/datasets/" + dataset + "/offset.txt"));
			int offset = Integer.parseInt(bufferedReader.readLine());
			return offset;

		} catch (FileNotFoundException e) {
			saveOffsetToFile(dataset, 1);
			return 1; 
		}		
	}
	
	

	/**
	 * 
	 * @param i
	 * @throws IOException
	 */
	private static void saveOffsetToFile(String dataset, int i) throws IOException {
		saveToFile("data/datasets/" + dataset + "/offset.txt", Integer.toString(i), false);
	}

	private static void saveLog(String string) throws IOException {
		saveToFile("data/log.txt", string, true);
	}

	/**
	 * 
	 * @param dataset
	 * @param records
	 * @param dateRange
	 * @throws IOException
	 */
	private static void saveSuccessfulPull(String dataset, String records, String dateRange) throws IOException {
		String toSave = dataset + ", " + records + " records, " + dateRange + "\n";
		saveToFile("data/successfulPulls.txt", toSave, true);
	}

	private static void saveClimateDataToFile(String data, String filePath) throws IOException {
		saveToFile(filePath, data, true);
		//TODO check if user wants to erase existing climate data on file
	}

	private static void saveToFile(String filePath, String fileData, Boolean append) throws IOException {
		Writer writer = new BufferedWriter(new FileWriter(filePath, append));
		PrintWriter out = new PrintWriter(writer);
		try {
			out.print(fileData);           
			out.flush();
			writer.flush();
		} finally {
			writer.close();
			out.close();
			//  return true;
		}		
	}
}
