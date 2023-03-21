import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import com.saxonica.xqj.SaxonXQDataSource;

public class mainRepas {

	public static void main(String[] args) throws IOException {
		String text, seleccionat;
		int posicio = 0;

		System.out.println("Quin text vols que contingui el titol de la pelicula?");

		Scanner sc = new Scanner(System.in);
		text = sc.nextLine();

		File BuscarPelis = new File("BuscarPelis.xqy");

		PrintWriter dataPelis = new PrintWriter(new FileOutputStream(new File("BuscarPelis.xqy"), false));

		try {

			dataPelis.println(
					"for $peli in doc(\"Peliculas2017.xml\")//pelicula/titulo[matches(text(),'" + text + "', 'i')]/text()\r\n" 
			+ "order by $peli\r\n" + "return $peli");

			dataPelis.flush();
			dataPelis.close();

			String xquery = "";
			HashMap<Integer, String> pelis = new HashMap<>();

			InputStream inputStream = Files.newInputStream(new File("BuscarPelis.xqy").toPath());
			XQDataSource ds = new SaxonXQDataSource();
			XQConnection conn = ds.getConnection();
			XQPreparedExpression exp = conn.prepareExpression(inputStream);
			XQResultSequence result = exp.executeQuery();

			while (result.next()) {
				posicio++;
				xquery = result.getItemAsString(null);
				pelis.put(posicio, xquery);
			}

			int[] opcions = new int[pelis.size()];

			System.out.println("Tria el numero de la peli");
			for (int i = 1; i < pelis.size(); i++) {

				System.out.println(i + " -> " + pelis.get(i));
			}

			Scanner sc2 = new Scanner(System.in);
			int numPeli = sc2.nextInt();

			File fitxer = new File("ConsultaPelis.xqy");
			PrintWriter data = new PrintWriter(new FileOutputStream(new File("ConsultaPelis.xqy"), false));

			try {
				data.println(
						"<html>\r\n<head>\r\n<title>Peliculas</title>\r\n</head>\r\n<body>\r\n<table border=\"1\">\r\n{");

				data.println(
						"for $peliculasGen in doc(\"Peliculas2017.xml\")//pelicula[titulo/text() = '" + pelis.get(numPeli) + "']\r\n" 
						+ "\r\nlet $titol := $peliculasGen/titulo/text()"
						+ "\r\nlet $any := $peliculasGen/fecha/text()"
						+ "\r\nlet $duracio := $peliculasGen/duracion/text()"
						+ "\r\norder by $any, $titol"
						+ "\r\nreturn"
						+ "\r\n<resultat>"
						+ "\r\n<tr>"
						+ "\r\n<td>"
						+ "\r\n<b>{$titol}</b>"
						+ "\r\n<ul>"
						+ "\r\n<li>Any: {$any}</li>"
						+ "\r\n<li>Duracio: {$duracio}</li>"
						+ "\r\n</ul>"
						+ "\r\n</td>"
						+ "\r\n</tr>"
						+ "\r\n</resultat>"
						+ "\r\n}"
						+ "\r\n</table>"
						+ "\r\n</body>"
						+ "\r\n</html>");
				
				data.flush();
				data.close();
				
				InputStream inputStream2 = Files.newInputStream(new File("ConsultaPelis.xqy").toPath());
				XQDataSource ds2 = new SaxonXQDataSource();
				XQConnection conn2 = ds2.getConnection();
				XQPreparedExpression exp2 = conn2.prepareExpression(inputStream2);
				XQResultSequence result2 = exp2.executeQuery();
				
				String xquery2 = "";
				while (result2.next()) {
					xquery2 = result2.getItemAsString(null);
				}
				
				PrintWriter out = new PrintWriter("output.html");
				out.println(xquery2);
				out.flush();
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
