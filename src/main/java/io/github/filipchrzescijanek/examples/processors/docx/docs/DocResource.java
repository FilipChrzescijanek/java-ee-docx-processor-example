package io.github.filipchrzescijanek.examples.processors.docx.docs;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Path("/docs")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class DocResource {

    @Inject
    DocRepository repository;

    @Context
    UriInfo uriInfo;

    @GET
    public Response getAll() {
        List<Doc> all = repository.getAll();
        return Response.ok()
                .entity(all)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Doc doc = repository.getById(id);
        return Response.ok()
                .entity(doc)
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(MultipartFormDataInput input) {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String docPart = input.getFormDataMap().get("doc").get(0).getBodyAsString();
            Doc doc = jsonb.fromJson(docPart, Doc.class);
            repository.create(doc);

            final URI location = uriInfo.getBaseUriBuilder()
                    .path(DocResource.class)
                    .path(doc.getId().toString())
                    .build();

            return Response.created(location)
                    .entity(doc)
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ConstraintViolationException e) {
            // pass to default handler
            throw e;
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response update(@PathParam("id") Long id, MultipartFormDataInput input) {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String docPart = input.getFormDataMap().get("doc").get(0).getBodyAsString();
            Doc doc = jsonb.fromJson(docPart, Doc.class);
            Doc updated = repository.update(id, doc);
            return Response.ok()
                    .entity(updated)
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ConstraintViolationException e) {
            // pass to default handler
            throw e;
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        repository.delete(id);
        return Response.noContent()
                .build();
    }

}