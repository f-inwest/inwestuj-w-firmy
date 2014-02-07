package eu.finwest.util;

import java.util.HashMap;
import java.util.List;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PresentationML.MainPresentationPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;

public class DocXHelper {
	private void replaceVariables(String inputfilepath, String outputfilepath, HashMap<String, String> mappings) {
		try {
			// Exclude context init from timing
			org.docx4j.wml.ObjectFactory foo = Context.getWmlObjectFactory();
		
			PresentationMLPackage wordMLPackage = PresentationMLPackage.load(new java.io.File(inputfilepath));
			MainPresentationPart documentPart = wordMLPackage.getMainPresentationPart();
	
			long start = System.currentTimeMillis();
	
//			documentPart.
//			List<Object> list = body.getContent();
			
			long end = System.currentTimeMillis();
			long total = end - start;
			System.out.println("Time: " + total);
	
			// Save it
			SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
			saver.save(outputfilepath);
		} catch (Docx4JException e) {
			e.printStackTrace();
		}
	}

}
