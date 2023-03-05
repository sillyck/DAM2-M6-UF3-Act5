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

public class main {

	public static void main(String[] args) throws IOException {
		HashMap<Integer, String> gen = new HashMap<>();
		int posicio = 0;
		try {
			InputStream inputStream = Files.newInputStream(new File("BuscaGeneros.xqy").toPath());
			XQDataSource ds = new SaxonXQDataSource();
			XQConnection conn = ds.getConnection();
			XQPreparedExpression exp = conn.prepareExpression(inputStream);
			XQResultSequence result = exp.executeQuery();

			String xquery = "";
			while (result.next()) {
				posicio++;
				xquery = result.getItemAsString(null);
				gen.put(posicio, xquery);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Scanner sc = new Scanner(System.in);
		int[] opcions = new int[gen.size()];
		int seleccionat = 1;
		int cont = 0;

		while (seleccionat != 0) {
			System.out.println("##########\nMENU\n##########");
			System.out.println("0 -> Sortir");
			for (int i = 1; i < gen.size(); i++) {

				System.out.println(i + " -> " + gen.get(i));
			}

			System.out.println("Escull un genere: ");
			seleccionat = sc.nextInt();
			if (seleccionat != 0) {
				opcions[cont] = seleccionat;

			}
			cont++;
		}
		for (int i = 0; i < cont - 1; i++) {
			System.out.println(opcions[i]);
		}
		File fitxer = new File("ConsultaGeneros.xqy");

		PrintWriter data = new PrintWriter(new FileOutputStream(new File("ConsultaGeneros.xqy"), false));
		
		try {
			data.println(
					"<html>\r\n<head>\r\n<title>Peliculas</title>\r\n</head>\r\n<body>\r\n<table border=\"1\">\r\n{");

			String stringGen = "";
			for (int i = 0; i < cont - 1; i++) {
				if (i + 1 != cont - 1) {
					stringGen = stringGen +"//genero = \"" + gen.get(opcions[i]) + "\" or ";
				} else {
					stringGen = stringGen +"//genero = \"" + gen.get(opcions[i]) + "\"";
				}
			}

			data.println(
					"for $peliculasGen in doc(\"Peliculas2017.xml\")//pelicula["
					+ stringGen + "]\r\n" 
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
			InputStream inputStream = Files.newInputStream(new File("ConsultaGeneros.xqy").toPath());
			XQDataSource ds = new SaxonXQDataSource();
			XQConnection conn = ds.getConnection();
			XQPreparedExpression exp = conn.prepareExpression(inputStream);
			XQResultSequence result = exp.executeQuery();
			
			String xquery2 = "";
			while (result.next()) {
				xquery2 = result.getItemAsString(null);
			}
			
			PrintWriter out = new PrintWriter("output.html");
			out.println(xquery2);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
