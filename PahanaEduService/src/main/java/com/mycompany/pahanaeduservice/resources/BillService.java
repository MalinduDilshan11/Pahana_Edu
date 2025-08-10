package com.mycompany.pahanaeduservice.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Utils.Bill;
import Utils.BillDB;
import Utils.LocalDateTimeAdapter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("bills")
public class BillService {

    private final Gson gson;

    public BillService() {
        // Initialize Gson with LocalDateTimeAdapter
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @GET
    @Path("/customer/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBillsByCustomer(@PathParam("customerId") int customerId) {
        BillDB billDB = new BillDB();
        List<Bill> bills = billDB.getBillsByCustomer(customerId);

        return Response
                .status(200)
                .entity(gson.toJson(bills))
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBillById(@PathParam("id") int id) {
        BillDB billDB = new BillDB();
        Bill bill = billDB.getBill(id);

        if (bill != null) {
            return Response.status(200).entity(gson.toJson(bill)).build();
        } else {
            return Response.status(404).entity("{\"message\": \"Bill not found\"}").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBill(String json) {
        Bill bill = gson.fromJson(json, Bill.class);

        BillDB billDB = new BillDB();
        boolean success = billDB.createBill(bill);

        if (success) {
            return Response.status(201).entity(gson.toJson(bill)).build();
        } else {
            return Response.status(500).entity("{\"message\": \"Error creating bill\"}").build();
        }
    }
}