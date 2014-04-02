package eu.finwest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.TraversalUtil;
import org.docx4j.TraversalUtil.Callback;
import org.docx4j.XmlUtils;
import org.docx4j.dml.CTTextBody;
import org.docx4j.dml.CTTextParagraph;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.PresentationML.SlideLayoutPart;
import org.docx4j.openpackaging.parts.PresentationML.SlidePart;
import org.docx4j.openpackaging.parts.WordprocessingML.ImageJpegPart;
import org.docx4j.utils.BufferUtil;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pptx4j.jaxb.Context;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import eu.finwest.dao.MockDataBuilder;
import eu.finwest.datamodel.Listing;
import eu.finwest.datamodel.SBUser;

public class OfficeHelper {
	private static final Logger log = Logger.getLogger(OfficeHelper.class.getName());
	
	private static final boolean DEBUG = false;
	private static final String BLACK_PRESENTATION_TEMPLATE = "./WEB-INF/email-templates/presentation_black.pptx";
	private static final String WHITE_PRESENTATION_TEMPLATE = "./WEB-INF/email-templates/presentation_white.pptx";
	
	private static final String PART_IMAGE = "/ppt/media/image1.jpg";
	private static final String PART_TITLE_SLIDE = "/ppt/slides/slide1.xml";
	private static final String PART_SLIDE = "/ppt/slides/slide2.xml";
	private static final String PART_SLIDE_LAYOUT = "/ppt/slideLayouts/slideLayout2.xml";
	
	public static enum Template {
		BLACK, WHITE
	};
	
	private static OfficeHelper instance;
	
	public static OfficeHelper instance() {
		if (instance == null) {
			instance = new OfficeHelper();
		}
		return instance;
	}
	
	private OfficeHelper() {
	}
	
	private String convertContent(String text) {
		return text;
	}
	
	public Map<String, String> getMappings(Listing listing, SBUser owner) {
		Map<String, String> mappings = new HashMap<String, String>();
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
		
		mappings.put("TITLE", listing.name);
		mappings.put("TITLE_WITH_DATE", listing.name + " " + fmt.print(listing.listedOn.getTime()));
		mappings.put("SUBTITLE_TOP", listing.mantra);
		mappings.put("MASTER_TITLE", owner.name + "\n" + listing.website);
		mappings.put("SUBTITLE_BOTTOM", listing.address + "\n"
				+ (listing.askedForFunding ?
						Translations.getText(listing.lang, "lang_asking") + " " + listing.suggestedAmount + " " + Translations.getText(listing.lang, "lang_for_equity") + " " + listing.suggestedPercentage + "%"
						: Translations.getText(listing.lang, "lang_not_asking_funds_now")));
		
		mappings.put("TITLE_2", Translations.getText(listing.lang, "lang_elevator_pitch"));
		mappings.put("CONTENT_2", convertContent(listing.answer27));
		mappings.put("TITLE_3", Translations.getText(listing.lang, "lang_the_problem"));
		mappings.put("CONTENT_3", convertContent(listing.answer10));
		mappings.put("TITLE_4", Translations.getText(listing.lang, "lang_the_solution"));
		mappings.put("CONTENT_4", convertContent(listing.answer11));
		mappings.put("TITLE_5", Translations.getText(listing.lang, "lang_features_and_benefits"));
		mappings.put("CONTENT_5", convertContent(listing.answer1));
		mappings.put("TITLE_6", Translations.getText(listing.lang, "lang_current_status"));
		mappings.put("CONTENT_6", convertContent(listing.answer13));
		mappings.put("TITLE_7", Translations.getText(listing.lang, "lang_the_market"));
		mappings.put("CONTENT_7", convertContent(listing.answer14));
		mappings.put("TITLE_8", Translations.getText(listing.lang, "lang_the_customer"));
		mappings.put("CONTENT_8", convertContent(listing.answer15));
		mappings.put("TITLE_9", Translations.getText(listing.lang, "lang_competitors"));
		mappings.put("CONTENT_9", convertContent(listing.answer16));
		mappings.put("TITLE_10", Translations.getText(listing.lang, "lang_competitive_comparison"));
		mappings.put("CONTENT_10", convertContent(listing.answer17));
		mappings.put("TITLE_11", Translations.getText(listing.lang, "lang_business_model"));
		mappings.put("CONTENT_11", convertContent(listing.answer18));
		mappings.put("TITLE_12", Translations.getText(listing.lang, "lang_marketing_plan"));
		mappings.put("CONTENT_12", convertContent(listing.answer19));
		mappings.put("TITLE_13", Translations.getText(listing.lang, "lang_the_team"));
		mappings.put("CONTENT_13", convertContent(listing.answer20));
		mappings.put("TITLE_14", Translations.getText(listing.lang, "lang_values_and_methods"));
		mappings.put("CONTENT_14", convertContent(listing.answer21));
		mappings.put("TITLE_15", Translations.getText(listing.lang, "lang_current_financials"));
		mappings.put("CONTENT_15", convertContent(listing.answer22));
		mappings.put("TITLE_16", Translations.getText(listing.lang, "lang_financial_projections"));
		mappings.put("CONTENT_16", convertContent(listing.answer23));
		mappings.put("TITLE_17", Translations.getText(listing.lang, "lang_current_ownership"));
		mappings.put("CONTENT_17", convertContent(listing.answer24));
		mappings.put("TITLE_18", Translations.getText(listing.lang, "lang_use_of_proceeds"));
		mappings.put("CONTENT_18", convertContent(listing.answer25));
		mappings.put("TITLE_19", Translations.getText(listing.lang, "lang_conclusion"));
		mappings.put("CONTENT_19", convertContent(listing.answer26));
		mappings.put("TITLE_20", Translations.getText(listing.lang, "lang_thank_you"));
		mappings.put("CONTENT_20", Translations.getText(listing.lang, "lang_please_contact") + "\n"
				+ owner.name + "\n" + listing.website + "\n" + listing.address);

		return mappings;
	}
	
	public PresentationMLPackage generatePresenation(Template template, InputStream logoStream, Map<String, String> mappings)
			throws IOException, JAXBException, Docx4JException {
		PresentationMLPackage pptMLPackage = PresentationMLPackage.load(
				new java.io.File(template == Template.BLACK ? BLACK_PRESENTATION_TEMPLATE : WHITE_PRESENTATION_TEMPLATE));

		if (DEBUG) {
			StringBuffer buf = new StringBuffer();
			buf.append("Available parts:").append("\n");
			for (PartName name : pptMLPackage.getParts().getParts().keySet()) {
				buf.append("   - " + name.getName()).append("\n");
			}
			log.info(buf.toString());
		}

		ImageJpegPart image = (ImageJpegPart)pptMLPackage.getParts().get(new PartName(PART_IMAGE));
		if (image == null) {
			log.log(Level.SEVERE, "Presentation template doesn't contain part " + PART_IMAGE);
		}
		image.setBinaryData(BufferUtil.readInputStream(logoStream));
		
		SlideLayoutPart slideTemplate = (SlideLayoutPart)pptMLPackage.getParts().get(new PartName(PART_SLIDE_LAYOUT));
		if (slideTemplate == null) {
			log.log(Level.SEVERE, "Presentation template doesn't contain part " + PART_SLIDE_LAYOUT);
		}
		
		int maxSlides = 1;
		for (int i = 2; mappings.containsKey("CONTENT_" + i); i++) {
			if (StringUtils.isNotBlank(mappings.get("CONTENT_" + i))) {
				maxSlides++;
			}
		}
		
		replaceSlideTokens(pptMLPackage, PART_TITLE_SLIDE, mappings);
		List<String> xmlParts = null;
		for (int i = 2, page = 2; mappings.containsKey("CONTENT_" + i); i++) {
			if (StringUtils.isBlank(mappings.get("CONTENT_" + i))) {
				continue;
			}
			
			mappings.put("PAGE_NUMBER", "" + page + " of " + maxSlides);
			mappings.put("TITLE", mappings.get("TITLE_" + i));
			mappings.put("CONTENT", mappings.get("CONTENT_" + i));
			if (xmlParts == null) {
				xmlParts = replaceSlideTokens(pptMLPackage, PART_SLIDE, mappings);
			} else {
				createSlideFromTemplate(pptMLPackage, slideTemplate, "/ppt/slides/slide" + page + ".xml", xmlParts, mappings);
			}
			page++;
		}

		//if (DEBUG) {
		//	debugSlideContent(pptMLPackage, slideTemplate);
		//}

		return pptMLPackage;
	}

	private List<String> replaceSlideTokens(PresentationMLPackage wordMLPackage, String partName, Map<String, String> mappings)
			throws InvalidFormatException, JAXBException, UnsupportedEncodingException {
		// unmarshallFromTemplate requires string input
		SlidePart part = (SlidePart)wordMLPackage.getParts().get(new PartName(partName));
		if (part == null) {
			log.log(Level.SEVERE, "Presentation template doesn't contain part " + partName);
		}

		List<String> xmls = new ArrayList<String>();
		
		List<Object> tokenized = new ArrayList<Object>();
		for (Object o : part.getJaxbElement().getCSld().getSpTree().getSpOrGrpSpOrGraphicFrame()) {
			String xml = XmlUtils.marshaltoString(o, true, org.pptx4j.jaxb.Context.jcPML);
			xmls.add(xml);
			for (Map.Entry<String, String> entry : mappings.entrySet()) {
				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					log.log(Level.SEVERE, "Value for key " + entry.getKey() + " is empty!");
					value = "[EMPTY VALUE]";
				}
				xml = xml.replace("&lt;" + entry.getKey() + "&gt;", value);
			}
			tokenized.add(XmlUtils.unmarshalString(xml, Context.jcPML));
		}
		part.getJaxbElement().getCSld().getSpTree().getSpOrGrpSpOrGraphicFrame().clear();
		part.getJaxbElement().getCSld().getSpTree().getSpOrGrpSpOrGraphicFrame().addAll(tokenized);
		return xmls;
	}

	private void createSlideFromTemplate(PresentationMLPackage mlPackage, SlideLayoutPart template, String partName, List<String> xmls, Map<String, String> mappings)
			throws InvalidFormatException, JAXBException, UnsupportedEncodingException {
		PresentationMLPackage.createSlidePart(mlPackage.getMainPresentationPart(), template, new PartName(partName));
		// unmarshallFromTemplate requires string input
		SlidePart part = (SlidePart)mlPackage.getParts().get(new PartName(partName));
		part.getJaxbElement().getCSld().getSpTree().getSpOrGrpSpOrGraphicFrame().clear();
		
		List<Object> tokenized = new ArrayList<Object>();
		for (String xml : xmls) {
			for (Map.Entry<String, String> entry : mappings.entrySet()) {
				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					log.log(Level.SEVERE, "Value for key " + entry.getKey() + " is empty!");
					value = "[EMPTY VALUE]";
				}
				xml = xml.replace("&lt;" + entry.getKey() + "&gt;", value);
			}
			tokenized.add(XmlUtils.unmarshalString(xml, Context.jcPML));
		}
		
		part.getJaxbElement().getCSld().getSpTree().getSpOrGrpSpOrGraphicFrame().addAll(tokenized);
	}

	private void debugSlideContent(PresentationMLPackage pptMLPackage,
			SlideLayoutPart slideTemplate) throws InvalidFormatException {
		log.fine("Full XML:\n\n"
			+ XmlUtils.marshaltoString(slideTemplate.getJaxbElement(), true, org.pptx4j.jaxb.Context.jcPML)
			+ "\n\nFull XML End\n\n");

		new TraversalUtil((Object) ((SlidePart)pptMLPackage.getParts().get(new PartName("/ppt/slides/slide1.xml"))).getJaxbElement().getCSld().getSpTree().getSpOrGrpSpOrGraphicFrame(),new Callback() {
			String indent = "";
	
			// @Override
			public List<Object> apply(Object o) {
				try {
					System.out.println(indent + o.getClass().getName() + "\n\n" + format(XmlUtils.marshaltoString(o, true, org.pptx4j.jaxb.Context.jcPML)));
				} catch (RuntimeException me) {
					log.log(Level.INFO, indent + o.getClass().getName(), me);
				}
	
				if (o instanceof org.pptx4j.pml.Shape) {
					log.fine("---- SHAPE --------------------------------------------");
					CTTextBody txBody = ((org.pptx4j.pml.Shape) o).getTxBody();
					if (txBody != null) {
						for (CTTextParagraph tp : txBody.getP()) {
							log.fine(indent + tp.getClass().getName() + "\n\n"
									+ XmlUtils.marshaltoString(tp, true, true, org.pptx4j.jaxb.Context.jcPML,
											"http://schemas.openxmlformats.org/presentationml/2006/main", "txBody", CTTextParagraph.class));
						}
					}
				}
	
				return null;
			}
	
			// @Override
			public boolean shouldTraverse(Object o) {
				return true;
			}
	
			// @Override
			public void walkJAXBElements(Object parent) {
				indent += "    ";
				List children = getChildren(parent);
				if (children != null) {
					for (Object o : children) {
						// if its wrapped in javax.xml.bind.JAXBElement, get its value
						o = XmlUtils.unwrap(o);
						this.apply(o);
						if (this.shouldTraverse(o)) {
							walkJAXBElements(o);
						}
					}
				}
				indent = indent.substring(0, indent.length() - 4);
			}
	
			// @Override
			public List<Object> getChildren(Object o) {
				return TraversalUtil.getChildrenImpl(o);
			}
		});
	}
	
	public static String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void main(String[] args) {
		Map<String, String> mappings = new HashMap<String, String>();
		
		/*
		mappings.put("TITLE", "Startupbidder");
		mappings.put("TITLE_WITH_DATE", "Startupbidder 2014-02-19 22:25pm");
		mappings.put("SUBTITLE_TOP", "Getting startups funded.");
		mappings.put("MASTER_TITLE", "by John A. Burns\nwww.startupbidder.com");
		mappings.put("SUBTITLE_BOTTOM", "Dusseldorf, Germany\n\nAsking $500.000 for 5%");
		
		mappings.put("TITLE_2", "Elevator Pitch");
		mappings.put("CONTENT_2", "Crowdfunding is exploding, but no one yet knows how to harness the power of crowdfunding for funding startups and mobile applications. We've done a lot of experimentation and research and we think we've cracked it. Now we want to bring our patentable method of crowdfunding not just to the USA but to every corner of the world. That's Startupbidder, getting startups funded.");
		mappings.put("TITLE_3", "The Problem");
		mappings.put("CONTENT_3", "* Traditionally, crowdfunding sites focus on pre-sales of products, which deny any upside for investors should the company become successful.\n* Furthermore, the large number of small investors, often numbering in the thousands, pose regulatory problems for companies seeking to sell shares in early investment rounds.");
		mappings.put("TITLE_4", "The Solution");
		mappings.put("CONTENT_4", "Startupbidder solves this problem by providing founders with direct capital from angel and venture capital investors.\n* Entrepreneurs list their company details and funding requirements\n* Dragons, our term for accredited investors, bid for investment \n* Both sides exchange bids back and forth until agreement is reached, and then sign a formal legal investment document ");
		mappings.put("TITLE_5", "Features and Benefits");
		mappings.put("CONTENT_5", "* Tools - business model, valuation, presentation, cap table, scenario tools \n* Funding - getting entrepreneurs funding \n* Investing - provide accredited investors startups and apps for investment");
		mappings.put("TITLE_6", "Current Status");
		mappings.put("CONTENT_6", "* Startupbidder is in live beta with an experienced exec team \n* Product is fully functional web site which also functions on mobile devices \n* Entrepreneurs are already signed up and posted listings \n* Several investors have joined and been approved for investments");
		mappings.put("TITLE_7", "The Market");
		mappings.put("CONTENT_7", "* 10 million accredited investors worldwide \n* 20% targeted adoption rate, which is 2 million investors \n* One $25,000 private placement investment per year \n* 2% fee for investment, $500/year per investor \n* $1 billion a year in target revenues");
		mappings.put("TITLE_8", "The Customer");
		mappings.put("CONTENT_8", "* Entrepreneurs - have a startup company, seeking funding, want faster turnaround than traditional VC route, want to spend time on selling to customers instead of selling to investors \n* Investors - want a larger breadth of potential investments to find a diamond in the rough, used to being on internet, mobile, boardroom presentations for investment seems slow and archaic, want to get in on the latest and greatest before anyone else");
		mappings.put("TITLE_9", "Competitors");
		mappings.put("CONTENT_9", "* Kickstarter - largest crowdfunding platform, $500m/year in raises, but can only do presales, not investments \n* Crowdcube - UK crowdfunding site allows actual investments, but restricted to only UK companies and investors with very high up-front fees that deter company listings \n* SharesPost - sells private pre-IPO shares to accredited investors through bidding, but only a handful of already expensive valuations with limited upside");
		mappings.put("TITLE_10", "Competitive Comparison");
		mappings.put("CONTENT_10", "* Most sites have a long and complex form for listing, we have a streamlined importing service from existing data that gets a company or application listed in under ten seconds. \n* Unlike most sites which target only the US or UK, we target each individual market in the world with customized services \n* Crowdfunding sites seldom offer any real investment, we are offering sales of actual preferred stock in businesses with very high potential upside.");
		mappings.put("TITLE_11", "Business Model");
		mappings.put("CONTENT_11", "* We get entrepreneurs with startup companies or mobile applications to visit our site and ask for funding, using a combination of advertising and personal engagement via email, phone and twitter \n* We get investors signed up based on the breadth of our investment offerings from the entrepreneurs \n* We keep both sides engaged through interaction, contests, challenges, hackathons, blogs, and success stories");
		*/
		
		try {
//			OfficeHelper.instance().helper.setUp();
			MockDataBuilder mocks = new MockDataBuilder();

			List<SBUser> users = mocks.createMockUsers();
			List<Listing> listings = mocks.createMockListings(users);
			
			InputStream logoStream = new FileInputStream("/Users/grzegorznittner/projects/inwestuj-w-firmy/tests/test-docs/logo11.jpg");
			mappings = OfficeHelper.instance().getMappings(listings.get(30), users.get(10));			
			PresentationMLPackage pptMLPackage = OfficeHelper.instance().generatePresenation(Template.BLACK, logoStream, mappings);
			pptMLPackage.save(new java.io.File("/Users/grzegorznittner/projects/inwestuj-w-firmy/tests/test-docs/presentation_black_modified.pptx"));
			
			logoStream = new FileInputStream("/Users/grzegorznittner/projects/inwestuj-w-firmy/tests/test-docs/logo5.jpg");
			mappings = OfficeHelper.instance().getMappings(listings.get(13), users.get(9));			
			pptMLPackage = OfficeHelper.instance().generatePresenation(Template.WHITE, logoStream, mappings);
			pptMLPackage.save(new java.io.File("/Users/grzegorznittner/projects/inwestuj-w-firmy/tests/test-docs/presentation_white_modified.pptx"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalTaskQueueTestConfig(),
//			new LocalUserServiceTestConfig(),
//			new LocalDatastoreServiceTestConfig().setNoStorage(true));

}
