import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import com.saxonica.xqj.SaxonXQDataSource;

public class examenFacturaPelis {

	public static void main(String[] args) throws IOException, XQException {
		HashMap<Integer, String> generos = new HashMap<>();
		HashMap<Integer, String> pelis = new HashMap<>();
		ArrayList<String> generosSeleccionados = new ArrayList<String>();
		ArrayList<String> pelisSeleccionados = new ArrayList<String>();
		String xquery = "";
		String pel = "";
		int posicion = 0;
		boolean contGen = false;
		boolean contPel = false;
		
		try {
			InputStream inputStream = Files.newInputStream(new File("BuscaGeneros.xqy").toPath());
			XQDataSource ds = new SaxonXQDataSource();
			XQConnection conn = ds.getConnection();
			XQPreparedExpression exp = conn.prepareExpression(inputStream);
			XQResultSequence result = exp.executeQuery();

			while (result.next()) {
				posicion++;
				xquery = result.getItemAsString(null);
				generos.put(posicion, xquery);
			}

		} catch (XQException | IOException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(System.in);
		while(contGen != true) {
			for (int i = 1; i <= generos.size(); i++) {
				System.out.println(i + ". " + generos.get(i));
			}
			System.out.println("0. salir");
			
			System.out.println("Selecciona el genero:");
			
			int genSel = sc.nextInt();
			
			if(genSel != 0) {
				System.out.println("Genero seleccionado: " + generos.get(genSel));
			
			
				PrintWriter fichero = new PrintWriter(new FileOutputStream("ConsultaPeli.xqy"));
		
				fichero.write("for $pel in doc(\"Peliculas2017.xml\")/peliculas/pelicula[generos/genero=\""+ generos.get(genSel)+"\"]\n"
						+ "let $title := $pel/titulo/text()\n"
						+ "order by $pel\n"
						+ "return $title");
		
				fichero.flush();
				fichero.close();
			}else {
				contGen = true;
				contPel = true;
			}
			
			if(contPel == false) {
				posicion = 0;
				try {
					InputStream inputStream = Files.newInputStream(new File("ConsultaPeli.xqy").toPath());
					XQDataSource ds = new SaxonXQDataSource();
					XQConnection conn = ds.getConnection();
					XQPreparedExpression exp = conn.prepareExpression(inputStream);
					XQResultSequence result = exp.executeQuery();
					
					pelis.clear();
					while (result.next()) {
						posicion++;
						xquery = result.getItemAsString(null);
						pelis.put(posicion, xquery);
					}
		
				} catch (XQException | IOException e) {
					e.printStackTrace();
				}
				
				
				for (int i = 1; i <= pelis.size(); i++) {
					System.out.println(i + ". " + pelis.get(i));
				}
				System.out.println("0. Volver");
				
				System.out.println("Selecciona la pelicula:");
				Scanner sc2 = new Scanner(System.in);
				
				int pelSel = sc.nextInt();
				if(pelSel != 0) {
					System.out.println("Pelicula seleccionada: " + pelis.get(pelSel) + "\n");
					pelisSeleccionados.add(pelis.get(pelSel));
				}
			}
		
		}
		
		PrintWriter fichero = new PrintWriter(new FileOutputStream("FacturaPelis.xqy"));

		for (int i = 0; i < pelisSeleccionados.size(); i++) {
			if (i + 1 != pelisSeleccionados.size()) {
				pel = pel.concat("titulo=\"" + pelisSeleccionados.get(i) + "\" or ");
			} else {
				pel = pel.concat("titulo=\"" + pelisSeleccionados.get(i) + "\"");
			}
		}
		
		//120 * 1 = 120 centimos / 100 = 1,20
		
		fichero.write("<html>\r\n" + "<head>\r\n" + "	<title>Peliculas</title>\r\n" + "</head\r\n>" + "<body>\r\n"
				+ "	<table border=\"1\">\r\n" + "{\r\n"
				+ "		for $pel in doc(\"Peliculas2017.xml\")/peliculas/pelicula[" + pel + "]\n"
				+ "		let $title := $pel/titulo/text()\n" 
				+ "		let $duracion := $pel/duracion/text()\n"
				+ "		let $precio := ($duracion*1) div 100\n"
				+ "		let $descuento := ($precio*10) div 100\n"
				+ "		let $precioDescontado := round($precio - $descuento, 2)\n"
//				+ "		let $precioTotal := $precioTotal + $precioDescontado\n"
				+ "		return\n" 
				+ "		<hola>\n" 
				+ "			<tr>\n" 
				+ "				<td>\n"
				+ "					<b>Pelicula</b>\n" 
				+ "				</td>\n" 
				+ "				<td>\n"
				+ "					<b>Duracion</b>\n" 
				+ "				</td>\n" 
				+ "				<td>\n"
				+ "					<b>Precio</b>\n" 
				+ "				</td>\n" 
				+ "			</tr>\n" 
				+ "			<tr>\n" 
				+ "				<td>\n"
				+ "					<center>{$title}</center>\n" 
				+ "				</td>\n" 
				+ "				<td>\n"
				+ "					<center>{$duracion}</center>\n" 
				+ "				</td>\n" 
				+ "				<td>\n"
				+ "					<center>{$precioDescontado}</center>\n" 
				+ "				</td>\n" 
				+ "			</tr>\n" 
				+ "		</hola>\n" 
				+ "}\n" 
				+ "	</table>\n"
				+ "</body>\n" 
				+ "</html>" 
				+ "");
		fichero.flush();
		fichero.close();

		InputStream inputStream = Files.newInputStream(new File("FacturaPelis.xqy").toPath());
		XQDataSource ds = new SaxonXQDataSource();
		XQConnection conn = ds.getConnection();
		XQPreparedExpression exp = conn.prepareExpression(inputStream);
		XQResultSequence result = exp.executeQuery();
		String resultado = "";
		while (result.next()) {
			resultado = result.getItemAsString(null);
		}
		PrintWriter out = new PrintWriter("Peliculas.html");
		out.println(resultado);
		out.flush();
		out.close();
	}

}