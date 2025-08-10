package com.mycompany.pahanaeduservice.resources;

import Utils.Order;
import Utils.OrderItem;
import Utils.Customer;
import Utils.CustomerDB;
import Utils.Item;
import Utils.ItemDB;
import Utils.OrderDB;
import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("orders")
public class OrderService {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(@QueryParam("accountNumber") String accountNumber, String json) {
        Gson gson = new Gson();
        Order order = gson.fromJson(json, Order.class);

        CustomerDB customerDB = new CustomerDB();
        ItemDB itemDB = new ItemDB();
        
        // Fetch customer by account number
        Customer customer = customerDB.getCustomerByAccountNumber(accountNumber);
        if (customer == null) {
            return Response.status(400).entity("{\"message\": \"Invalid account number\"}").build();
        }
        order.setCustomerId(customer.getCustomerId());

        // Validate items and stock
        List<Item> items = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = itemDB.getItem(orderItem.getItemId());
            if (item == null || item.getStockQuantity() < orderItem.getUnitsConsumed()) {
                return Response.status(400).entity("{\"message\": \"Invalid item or insufficient stock\"}").build();
            }
            items.add(item);
        }

        OrderDB orderDB = new OrderDB();
        boolean success = orderDB.createOrder(order, customer, items);

        if (success) {
            return Response.status(201).entity("{\"message\": \"Order created successfully\"}").build();
        } else {
            return Response.status(400).entity("{\"message\": \"Failed to create order\"}").build();
        }
    }

    @GET
    @Path("/customer/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByCustomerId(@PathParam("customerId") int customerId) {
        OrderDB orderDB = new OrderDB();
        return Response.ok(new Gson().toJson(orderDB.getOrdersByCustomerId(customerId))).build();
    }
}