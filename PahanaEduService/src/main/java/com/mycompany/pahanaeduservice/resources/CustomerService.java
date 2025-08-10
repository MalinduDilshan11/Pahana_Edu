package com.mycompany.pahanaeduservice.resources;

import com.google.gson.Gson;
import Utils.Customer;
import Utils.CustomerDB;
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

@Path("customers")
public class CustomerService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers() {
        CustomerDB customerDB = new CustomerDB();
        List<Customer> customers = customerDB.getCustomers();

        Gson gson = new Gson();
        return Response
                .status(200)
                .entity(gson.toJson(customers))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomer(String json) {
        Gson gson = new Gson();
        Customer customer = gson.fromJson(json, Customer.class);

        CustomerDB customerDB = new CustomerDB();
        boolean success = customerDB.addCustomer(customer);

        if (success) {
            return Response.status(201).entity(gson.toJson(customer)).build();
        } else {
            return Response.status(500).entity("{\"message\": \"Error adding customer\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") int id, String json) {
        Gson gson = new Gson();
        Customer customer = gson.fromJson(json, Customer.class);
        customer.setCustomerId(id); // Ensure the ID from the path is set

        CustomerDB customerDB = new CustomerDB();
        boolean success = customerDB.updateCustomer(customer);

        if (success) {
            return Response.status(200).entity(gson.toJson(customer)).build();
        } else {
            return Response.status(500).entity("{\"message\": \"Error updating customer\"}").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("id") int id) {
        CustomerDB customerDB = new CustomerDB();
        boolean success = customerDB.deleteCustomer(id);

        if (success) {
            return Response.status(200).entity("{\"message\": \"Customer deleted successfully\"}").build();
        } else {
            return Response.status(404).entity("{\"message\": \"Customer not found or error deleting customer\"}").build();
        }
    }
   

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") int id) {
        CustomerDB customerDB = new CustomerDB();
        Customer customer = customerDB.getCustomer(id);

        if (customer != null) {
            Gson gson = new Gson();
            return Response.status(200).entity(gson.toJson(customer)).build();
        } else {
            return Response.status(404).entity("{\"message\": \"Customer not found\"}").build();
        }
    }

    @GET
    @Path("/account/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerByAccountNumber(@PathParam("accountNumber") String accountNumber) {
        CustomerDB customerDB = new CustomerDB();
        Customer customer = customerDB.getCustomerByAccountNumber(accountNumber);

        if (customer != null) {
            Gson gson = new Gson();
            return Response.status(200).entity(gson.toJson(customer)).build();
        } else {
            return Response.status(404).entity("{\"message\": \"Customer not found\"}").build();
        }
    }
}