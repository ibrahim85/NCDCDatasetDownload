## Synopsis

This Java application connects to NOAA's web services in order to obtain **climate data** from the US. In order to run it, a token from the NOAA Climatic Data Center must be obtained. You can do so (https://www.ncdc.noaa.gov/cdo-web/token "here").

## Code Example

<pre><code>
String[] datasets = {"PRECIP_HLY", "NORMAL_MLY"};
String[] dateRange = {"2010-01-01", "2010-01-05"};
String token = "insert your NCDC web services token here";
		 
// Instructions:
// 1) Delete files inside data/datasets 
// 2) Delete data/successfulPulls.txt file
			
DatasetDownloader datasetsDownloader = new DatasetDownloader(token, datasets, dateRange, true);
</code></pre>

Add the datasets you want to download data from to String[] datasets. You can fetch a list of all available datasets through (http://www.ncdc.noaa.gov/cdo-web/api/v2/datasets "here").

