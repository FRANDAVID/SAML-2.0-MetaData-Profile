package com.wso2.training.fasttract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*10,      // 10MB
                 maxRequestSize=1024*1024*50)   // 50MB
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIR = "uploadedFiles";
	private String savedFilePath;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
  

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String appPath = request.getServletContext().getRealPath("");
		
		String savePath = appPath + File.separator + SAVE_DIR;
		//String savePath = "/home/rajee/Desktop";
		String fileName = null;
		
		
		System.out.println(savePath);
		File saveFile = new File(savePath);
		if(!saveFile.exists())
		{
			saveFile.mkdir();
		}
		for (Part part : request.getParts()) {
            fileName = extractFileName(part);
            System.out.println(fileName);
            savedFilePath = savePath + File.separator + fileName;
            part.write(savedFilePath);
        }
		readFile(savedFilePath, request);
		
		try {
			getDocumentAsXml(savedFilePath,request);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("message", "Upload has been done successfully!");
		System.out.println(request.getAttribute("message"));
        getServletContext().getRequestDispatcher("/message.jsp").forward(
                request, response);
        
        
        
        
	}
	public void readFile(final String path, HttpServletRequest request) {
		try {
			
		BufferedReader br  = new BufferedReader(new FileReader(path));
		String sCurrentLine;
		StringBuilder builder = new StringBuilder();
		
			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine);
				
				
			}
			System.out.println(builder.toString());
			request.setAttribute("file", builder.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	public void getDocumentAsXml(final String filepath,HttpServletRequest request) throws TransformerConfigurationException, TransformerException, SAXException, IOException, ParserConfigurationException {
		
		File retrievedFile = new File(filepath);
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(retrievedFile);
		document.getDocumentElement().normalize();
		System.out.println(document.getDocumentElement().getNodeName());
		
		request.setAttribute("retrivedfile",document.getDocumentElement().getNodeName());
		
		  
		}
}
