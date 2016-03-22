import java.io.File;
import java.io.IOException;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Main {


	public static void main(String[] args) throws IOException, UnirestException {
		String[] datasets = {"PRECIP_HLY", "NORMAL_MLY"}; 
		String[] dateRange = {"2010-01-01", "2010-01-03"};
		String token = "Enter your token here";
		 
		// Instructions:
		// 1) If you've changed the datasets array, manually delete files inside data/datasets
		// 2) Delete data/successfulPulls.txt file
		
		
		DatasetDownloader datasetsDownloader = new DatasetDownloader(token, datasets, dateRange, false);
		
			
	}
	
}
