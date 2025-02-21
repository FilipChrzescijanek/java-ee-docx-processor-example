package io.github.filipchrzescijanek.examples.processors.docx.docs;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunAsClient
@ExtendWith(ArquillianExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocResourceIT {

    @Test
    @Order(1)
    public void create() {
        try (Client client = ClientBuilder.newClient()) {
            Doc doc = new Doc("Test Doc title", "Test Doc author", "Test Doc contents");

            MultipartFormDataOutput form = new MultipartFormDataOutput();
            form.addFormData("doc", doc, MediaType.APPLICATION_JSON_TYPE);

            Response response = client
                    .target(URI.create("http://localhost:8080/"))
                    .path("/api/docs")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

            assertEquals(201, response.getStatus());
            assertEquals("http://localhost:8080/api/docs/1", response.getLocation().toString());
        }
    }

    @Test
    @Order(2)
    public void list() {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(URI.create("http://localhost:8080/"))
                    .path("/api/docs")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());

            List<Doc> docs = response.readEntity(new GenericType<>() {
            });
            assertEquals(1, docs.size());

            Doc doc = docs.get(0);
            assertEquals("Test Doc title", doc.getTitle());
            assertEquals("Test Doc author", doc.getAuthor());
            assertEquals("Test Doc contents", doc.getContents());
        }
    }

    @Test
    @Order(3)
    public void update() {
        try (Client client = ClientBuilder.newClient()) {
            Doc doc = new Doc("Test Doc title updated", "Test Doc author updated", "Test Doc contents updated");

            MultipartFormDataOutput form = new MultipartFormDataOutput();
            form.addFormData("doc", doc, MediaType.APPLICATION_JSON_TYPE);

            Response response = client
                    .target(URI.create("http://localhost:8080/"))
                    .path("/api/docs/1")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

            assertEquals(200, response.getStatus());

            response = client
                    .target(URI.create("http://localhost:8080/"))
                    .path("/api/docs/1")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            Doc updated = response.readEntity(new GenericType<>() {
            });

            assertEquals("Test Doc title updated", updated.getTitle());
            assertEquals("Test Doc author updated", updated.getAuthor());
            assertEquals("Test Doc contents updated", updated.getContents());
        }
    }

    @Test
    @Order(4)
    public void delete() {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(URI.create("http://localhost:8080/"))
                    .path("/api/docs/1")
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            assertEquals(204, response.getStatus());

            response = client
                    .target(URI.create("http://localhost:8080/"))
                    .path("/api/docs/1")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }
    }

}