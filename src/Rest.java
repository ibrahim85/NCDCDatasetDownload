import java.io.*;
import java.net.*;

public class Rest {
	private URL url; // = new URL(INSERT_HERE_YOUR_URL);
	private String query; // = INSERT_HERE_YOUR_URL_PARAMETERS;
	private String token;

	public Rest(String url, String query, String token) throws MalformedURLException {
		this.url = new URL(url);
		this.query = query;
		this.token = token;
	}
	
	public  void connect() throws IOException {
		URLConnection urlc = url.openConnection();

		//use post mode
		urlc.setDoOutput(true);
		urlc.setAllowUserInteraction(false);
		
		//send query
		PrintStream ps = new PrintStream(urlc.getOutputStream());
		ps.print(query);
		ps.close();

		//get result
		BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
//		String l = null;
//		while ((l=br.readLine())!=null) {
//			System.out.println(l);
//		}
		//br.close();
	}
}