package com.example.ocelotnovels.model;

/**
 * This is a class that will store the location of images in the database so that the admin can access them in a browser
 */
public class Image {
    private String collection;
    private String document;
    private String url;

    /**
     * The constructor for the Image class
     * @param collection the collection that the image is located in firestore
     * @param document the document that the image is located in in firestore
     * @param url the url of the image
     */
    public Image(String collection, String document, String url){
        this.collection = collection;
        this.document = document;
        this.url = url;
    }

    /**
     * this return the collection that the image is found in in firestore
     * @return the String for the collection
     */
    public String getCollection() {
        return collection;
    }

    /**
     * this return the document that the image is found in in firestore
     * @return the String for the document
     */
    public String getDocument() {
        return document;
    }

    /**
     * this return the collection that the image is found in in firestore
     * @return the String for the URL
     */
    public String getUrl() {
        return url;
    }
}
