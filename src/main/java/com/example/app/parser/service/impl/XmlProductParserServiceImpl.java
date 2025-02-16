package com.example.app.parser.service.impl;

import com.example.app.entity.Product;
import com.example.app.parser.service.ProductParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class XmlProductParserServiceImpl implements ProductParserService {

    @Override
    public Flux<Product> parse(MultipartFile file) {
        try {
            Document document = buildDocument(file);
            List<Product> products = extractProducts(document);
            return Flux.fromIterable(products);
        } catch (Exception e) {
            log.error("Error parsing XML file: {}", e.getMessage(), e);
            return Flux.empty();
        }
    }

    /**
     * Builds a normalized DOM Document from the provided XML file.
     */
    private Document buildDocument(MultipartFile file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file.getInputStream());
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Extracts a list of Product objects from the DOM Document.
     */
    private List<Product> extractProducts(Document document) {
        List<Product> products = new ArrayList<>();
        NodeList productNodes = document.getElementsByTagName("product");

        for (int i = 0; i < productNodes.getLength(); i++) {
            Node node = productNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Product product = createProductFromElement(element);
                if (product != null) {
                    products.add(product);
                }
            }
        }
        return products;
    }

    /**
     * Creates a Product from an XML element.
     */
    private Product createProductFromElement(Element element) {
        String productIdText = getTagValue("productId", element);
        String productName = getTagValue("productName", element);

        try {
            int productId = Integer.parseInt(productIdText);
            return Product.builder()
                    .productId(productId)
                    .productName(productName)
                    .build();
        } catch (NumberFormatException e) {
            log.error("Invalid productId: {}", productIdText, e);
            return null;
        }
    }

    /**
     * Helper method to extract the text content of the specified tag from an element.
     */
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }
}
