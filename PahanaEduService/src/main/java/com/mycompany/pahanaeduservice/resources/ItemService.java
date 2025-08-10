package com.mycompany.pahanaeduservice.resources;

import com.google.gson.Gson;
import Utils.Item;
import Utils.ItemDB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;


@Path("items")
public class ItemService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        ItemDB itemDB = new ItemDB();
        List<Item> items = itemDB.getItems();

        Gson gson = new Gson();
        return Response
                .status(200)
                .entity(gson.toJson(items))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItem(String json) {
        Gson gson = new Gson();
        Item item = gson.fromJson(json, Item.class);

        ItemDB itemDB = new ItemDB();
        boolean success = itemDB.addItem(item);

        if (success) {
            return Response.status(201).entity(gson.toJson(item)).build();
        } else {
            return Response.status(500).entity("{\"message\": \"Error adding item\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItem(@PathParam("id") int id, String json) {
        Gson gson = new Gson();
        Item item = gson.fromJson(json, Item.class);
        item.setItemId(id); // Ensure the ID from the path is set

        ItemDB itemDB = new ItemDB();
        boolean success = itemDB.updateItem(item);

        if (success) {
            return Response.status(200).entity(gson.toJson(item)).build();
        } else {
            return Response.status(500).entity("{\"message\": \"Error updating item\"}").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteItem(@PathParam("id") int id) {
        ItemDB itemDB = new ItemDB();
        boolean success = itemDB.deleteItem(id);

        if (success) {
            return Response.status(200).entity("{\"message\": \"Item deleted successfully\"}").build();
        } else {
            return Response.status(404).entity("{\"message\": \"Item not found or error deleting item\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("id") int id) {
        ItemDB itemDB = new ItemDB();
        Item item = itemDB.getItem(id);

        if (item != null) {
            Gson gson = new Gson();
            return Response.status(200).entity(gson.toJson(item)).build();
        } else {
            return Response.status(404).entity("{\"message\": \"Item not found\"}").build();
        }
    }
}