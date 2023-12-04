import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dates {

	static SimpleDateFormat input = new SimpleDateFormat("d MMM yyyy, h:mm a");
	static SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+10:00");

	public static void main(String... args) throws IOException {
		FileReader fileReader = new FileReader(
				"c:\\users\\sander\\Desktop\\anonymous - Chapter 2_detailed.csv");

		// Always wrap FileReader in BufferedReader.
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {

			String[] a = line.split(",");

			//we have a date

			Date complete;
			try {
				if (a.length > 1) {
					complete = parseDate(a[8].substring(1) + "," + a[9]);

					//System.out.print(a[0]);
					//System.out.print(output.format(complete));

					for (int i = 0; i < 8; i++) {
						System.out.print(a[i] + ",");
					}
					System.out.print(output.format(complete));
					for (int i = 10; i < a.length; i++) {
						System.out.print("," + a[i]);
					}
					System.out.println();

//					String[] b = line.split(",");
//					double s = Double.parseDouble(b[6]);
//
//					complete.setTime(complete.getTime() - Math.round(s * 1000));
//
//					System.out.print(",");
//					System.out.println(output.format(complete));
				}
			} catch (ParseException e) {
			}
		}

		// Always close files.
		bufferedReader.close();
	}

	public static Date parseDate(String date) throws ParseException {

		date = date.replace("st", "");
		date = date.replace("nd", "");
		date = date.replace("rd", "");
		date = date.replace("th", "");

		return input.parse(date);
	}
}
