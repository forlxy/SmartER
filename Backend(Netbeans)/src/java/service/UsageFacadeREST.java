/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import restws.Resident;
import restws.Usage;

/**
 *
 * @author kasal
 */
@Stateless
@Path("restws.usage")
public class UsageFacadeREST extends AbstractFacade<Usage> {

    @PersistenceContext(unitName = "AssignmentPU")
    private EntityManager em;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private java.util.Date date;
    
    public UsageFacadeREST() {
        super(Usage.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Usage entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Usage entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }
    
    
    //Static query by usage id
    @GET
    @Path("findByUsageid/{usageid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByUsageid(@PathParam("usageid") Integer usageid) {
        Query query = em.createNamedQuery("Usage.findByUsageid");
        query.setParameter("usageid", usageid);
        return query.getResultList();
    }
    
    //Static query by usage's date
    //Data should in form like yyyy-MM-dd
    @GET
    @Path("findByUsagedate/{usagedate}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByUsagedate(@PathParam("usagedate") String usagedate) throws ParseException {
        Query query = em.createNamedQuery("Usage.findByUsagedate");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date;
        date = new java.util.Date( df.parse( usagedate ).getTime() ); 
        query.setParameter("usagedate", date);
        return query.getResultList();
    }
    
    //Static query by usage's hour
    @GET
    @Path("findByHours/{hours}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByHours(@PathParam("hours") Integer hours) {
        Query query = em.createNamedQuery("Usage.findByHours");
        query.setParameter("hours", hours);
        return query.getResultList();
    }
    
    //Static query by fridge usage
    @GET
    @Path("findByFridge/{fridge}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByFridge(@PathParam("fridge") Double fridge) {
        Query query = em.createNamedQuery("Usage.findByFridge");
        query.setParameter("fridge", fridge);
        return query.getResultList();
    }
    
    //Static query by air conditioner usage
    @GET
    @Path("findByAircond/{aircond}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByAircond(@PathParam("aircond") Double aircond) {
        Query query = em.createNamedQuery("Usage.findByAircond");
        query.setParameter("aircond", aircond);
        return query.getResultList();
    }
    
    //Static query by wash machine usage
    @GET
    @Path("findByWashmach/{washmach}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByWashmach(@PathParam("washmach") Double washmach) {
        Query query = em.createNamedQuery("Usage.findByWashmach");
        query.setParameter("washmach", washmach);
        return query.getResultList();
    }
    
    //Static query by usage's termperature
    @GET
    @Path("findByTemperature/{temperature}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByTemperature(@PathParam("temperature") Double temperature) {
        Query query = em.createNamedQuery("Usage.findByTemperature");
        query.setParameter("temperature", temperature);
        return query.getResultList();
    }
    
    //Static query by usage's resident id (foreign key)
    @GET
    @Path("findByResid/{resid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findByResid(@PathParam("resid") Integer resid) {
        Query query = em.createNamedQuery("Usage.findByResid");
        query.setParameter("resid", resid);
        return query.getResultList();
    }
       
    //Dynamic query by surname and firstname of resident using implicit join between USAGE and RESIDENT
    @GET
    @Path("findByFnameSname/{fname}/{sname}")
    @Produces({"application/json"})
    public List<Usage> findByFnameSname(@PathParam("fname") String fname, @PathParam("sname") String sname) {
        TypedQuery<Usage> query = em.createQuery("SELECT u FROM Usage u WHERE UPPER(u.resid.fname) = UPPER(:fname) "
                + "AND UPPER(u.resid.sname) = UPPER(:sname)", Usage.class);
        query.setParameter("fname", fname);
        query.setParameter("sname", sname);
        return query.getResultList();
    } 
    
    //Static query by address and mobile of resident using implicit join between USAGE and RESIDENT
    @GET
    @Path("findByAddressMobile/{address}/{mobile}")
    @Produces({"application/json"})
    public List<Usage> findByAddressMobile(@PathParam("address") String address, @PathParam("mobile") String mobile) {
        Query query = em.createNamedQuery("Usage.findByAddressMobile");
        query.setParameter("address", address);
        query.setParameter("mobile", mobile);
        return query.getResultList();
    } 
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usage find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usage> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    //Tool function for rounding double
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    //task4 1
    //hourly power usage of specified appliance
    //appliance must contain "fridge" or "air" or "conditioner" or "wash" to identify appliance
    //Data should in form like yyyy-MM-dd
    //use jsonarray to reconstruct returned list
    @GET
    @Path("getSingleHourlyUsage/{resid}/{appliance}/{usagedate}/{hour}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Object getSingleHourlyUsage(@PathParam("resid") int resid, @PathParam("appliance") String appliance, 
            @PathParam("usagedate") String usagedate, @PathParam("hour") int hour) throws ParseException {
        String ak = null;
        if(appliance.contains("fridge"))
            ak = "fridge";
        else if(appliance.contains("condioner") || appliance.contains("air"))
            ak = "aircond";
        else if(appliance.contains("wash"))
            ak = "washmach";               
        TypedQuery<Double> query = em.createQuery("SELECT u." + ak + " FROM Usage u WHERE u.resid.resid = :resid "
                + "AND u.usagedate = :usagedate AND u.hours = :hour", Double.class);
        date = new java.util.Date( df.parse( usagedate ).getTime() );       
        query.setParameter("resid", resid);
        query.setParameter("usagedate", date);
        query.setParameter("hour", hour);   
        List<Double> queryResult = query.getResultList();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Double row : queryResult)
        {
            JsonObject personObject = Json.createObjectBuilder().
                            add(ak, row).build();
            arrayBuilder.add(personObject);
        }
        JsonArray jArray = arrayBuilder.build();
        return jArray;
    } 
    
    //task4 2
    //hourly power usage of ALL appliances 
    //Data should in form like yyyy-MM-dd
    //use jsonarray to reconstruct returned list
    @GET
    @Path("getHourlyUsage/{resid}/{usagedate}/{hour}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Object getHourlyUsage(@PathParam("resid") int resid, @PathParam("usagedate") String usagedate, @PathParam("hour") int hour) throws ParseException {
        TypedQuery<Double> query = em.createQuery("SELECT (u.aircond + u.fridge + u.washmach) as Totalusage FROM Usage u WHERE u.resid.resid = :resid "
                + "AND u.usagedate = :usagedate AND u.hours = :hour", Double.class);
        date = new java.util.Date( df.parse( usagedate ).getTime() ); 
        query.setParameter("resid", resid);
        query.setParameter("usagedate", date);
        query.setParameter("hour", hour);
        List<Double> queryResult = query.getResultList();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Double row : queryResult)
        {
            
            JsonObject personObject = Json.createObjectBuilder().
                            add("Totalusage", row).build();
            arrayBuilder.add(personObject);
        }
        JsonArray jArray = arrayBuilder.build();
        return jArray;
    } 
    
//    public String convertDateToString(Date d)
//    {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String reportDate = df.format(d);
//        return reportDate;
//    }
    
    //task4 3
    //hourly power usage of All appliances for all residents
    //Data should in form like yyyy-MM-dd
    //use jsonarray to reconstruct returned list
    @GET
    @Path("getListHourlyUsage/{usagedate}/{hour}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Object getListHourlyUsage(@PathParam("usagedate") String usagedate, @PathParam("hour") int hour) throws ParseException {
        TypedQuery<Object[]> query = em.createQuery("SELECT u.resid.resid,u.resid.address,u.resid.postcode,(u.aircond + u.fridge + u.washmach) as Totalusage FROM Usage u WHERE u.usagedate = :usagedate AND "
                + "u.hours = :hour", Object[].class);
        date = new java.util.Date( df.parse( usagedate ).getTime() ); 
        query.setParameter("usagedate", date);
        query.setParameter("hour", hour);
        List<Object[]> queryResult = query.getResultList();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Object[] row : queryResult)
        {
            JsonObject personObject = Json.createObjectBuilder().
                            add("resid", row[0].toString())
                           .add("address", (String)row[1])
                           .add("postcode", (String)row[2])
                           .add("total", row[3].toString()).build();
            arrayBuilder.add(personObject);
        }
        JsonArray jArray = arrayBuilder.build();
        return jArray;
    } 
    
    //task4 4
    //highest hourly power consumption
    //first get the maximum of usage
    //then select other attributes with the maximum 
    //use jsonarray to reconstruct returned list
    @GET
    @Path("getHighestConsumption/{resid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Object getHighestConsumption(@PathParam("resid") int resid) throws ParseException {
        TypedQuery<Double> query = em.createQuery("SELECT max(u.aircond + u.fridge + u.washmach) FROM Usage u WHERE u.resid.resid = :resid", Double.class);
        query.setParameter("resid", resid);
        double max = 0.f;
        max = query.getSingleResult();
        TypedQuery<Object[]> query2 = em.createQuery("SELECT u.usagedate,u.hours, (u.aircond + u.fridge + u.washmach) as Totalusage FROM Usage u WHERE (u.aircond + u.fridge + u.washmach) = :max AND u.resid.resid = :resid", Object[].class);
        query2.setParameter("max", max);
        query2.setParameter("resid", resid);
        List<Object[]> queryResult = query2.getResultList();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Object[] row : queryResult)
        {
            JsonObject personObject = Json.createObjectBuilder().
                            add("usagedate", row[0].toString())
                           .add("hours", row[1].toString())
                           .add("total", row[2].toString())
                           .build();
            arrayBuilder.add(personObject);
        }
        JsonArray jArray = arrayBuilder.build();
        return jArray;
    } 
    
    //task5 1
    //Daily Usage of Appliances
    //select three total usage by using sum function in sql code
    //Data should in form like yyyy-MM-dd
    //use jsonarray to reconstruct returned list
    @GET
    @Path("getDailyUsageAppliance/{resid}/{usagedate}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Object getDailyUsageAppliance(@PathParam("resid") int resid, @PathParam("usagedate") String usagedate) throws ParseException {
        TypedQuery<Object[]> query = em.createQuery("SELECT sum(u.fridge),sum(u.aircond),sum(u.washmach) FROM Usage u WHERE u.resid.resid = :resid AND u.usagedate = :usagedate", Object[].class);
        date = new java.util.Date( df.parse( usagedate ).getTime() ); 
        query.setParameter("resid", resid);
        query.setParameter("usagedate", date);
        List<Object[]> queryResult = query.getResultList();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        double frisum = 0, airsum = 0, washsum = 0; 
        for (Object[] row : queryResult)
        {
            JsonObject personObject = Json.createObjectBuilder().
                                add("resid", resid)
                               .add("fridge", round((double)row[0], 2))
                               .add("aircon", round((double)row[1], 2))
                               .add("washingmachine", round((double)row[2], 2))
                               .build();
                arrayBuilder.add(personObject);
        }
        JsonArray jArray = arrayBuilder.build();
        return jArray;
    } 
    
    //task5 2
    //Hourly/Daily Usage
    //view can only be "hourly" or "daily" 
    //average of temperature is calculated by using avg function in sql code
    //usage in every hour got by using group by key word in sql
    //Data should in form like yyyy-MM-dd
    //use jsonarray to reconstruct returned list
    @GET
    @Path("getHourDailyUsage/{resid}/{usagedate}/{view}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public JsonArray getHourDailyUsage(@PathParam("resid") int resid, @PathParam("usagedate") String usagedate,@PathParam("view") String view) throws ParseException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if(view.equalsIgnoreCase("hourly")) {
            for(int i = 0; i < 24; i++)
            {
                TypedQuery<Object[]> query = em.createQuery("SELECT u.resid.resid,(u.aircond + u.fridge + u.washmach) as usage,u.temperature,u.usagedate,u.hours FROM Usage u WHERE u.usagedate = :usagedate AND "
                        + "u.resid.resid = :resid AND u.hours = :hours", Object[].class);
                date = new java.util.Date( df.parse( usagedate ).getTime() ); 
                query.setParameter("usagedate", date);
                query.setParameter("resid", resid);
                query.setParameter("hours",i);
                List<Object[]> queryResult = query.getResultList();
                for (Object[] row : queryResult)
                {
                        JsonObject personObject = Json.createObjectBuilder().
                                        add("resid", row[0].toString())
                                       .add("usage", round((double)row[1], 2))
                                       .add("temperature", row[2].toString())
                                       .add("usagedate", row[3].toString())
                                       .add("hours", row[4].toString()).build();
                        arrayBuilder.add(personObject);
                }
            }
        }
        else if(view.equalsIgnoreCase("daily")) {
            TypedQuery<Object[]> query = em.createQuery("SELECT u.resid.resid,sum(u.aircond + u.fridge + u.washmach) as usage,avg(u.temperature) FROM Usage u WHERE u.usagedate = :usagedate AND "
                    + "u.resid.resid = :resid Group by u.resid.resid", Object[].class);
            date = new java.util.Date( df.parse( usagedate ).getTime() ); 
            query.setParameter("usagedate", date);
            query.setParameter("resid", resid);
            List<Object[]> queryResult = query.getResultList();
            for (Object[] row : queryResult)
            {
                    JsonObject personObject = Json.createObjectBuilder().
                                add("resid", row[0].toString())
                               .add("usage", round((double)row[1], 2))
                               .add("temperature", round((double)row[2], 2))
                               .build();
                    arrayBuilder.add(personObject);
            }
        }
        else {
            JsonObject personObject = Json.createObjectBuilder().
                            add("error", "You can only input hourly or daily for view variable!").build();
            arrayBuilder.add(personObject);        
        }     
        JsonArray jArray = arrayBuilder.build();     
        return jArray;
    } 
    
    //For android
    
    @GET
    @Path("getHourDailyUsageForReport/{resid}/{usagedate}/{view}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public JsonArray getHourDailyUsageForReport(@PathParam("resid") int resid, @PathParam("usagedate") String usagedate,@PathParam("view") String view) throws ParseException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        if(view.equalsIgnoreCase("hourly")) {
            for(int i = 0; i < 24; i++)
            {
                TypedQuery<Object[]> query = em.createQuery("SELECT u.resid.resid,(u.aircond + u.fridge + u.washmach) as usage,u.temperature,u.usagedate,u.hours FROM Usage u WHERE u.usagedate = :usagedate AND "
                        + "u.resid.resid = :resid AND u.hours = :hours", Object[].class);
                date = new java.util.Date( df.parse( usagedate ).getTime() ); 
                query.setParameter("usagedate", date);
                query.setParameter("resid", resid);
                query.setParameter("hours",i);
                List<Object[]> queryResult = query.getResultList();
                for (Object[] row : queryResult)
                {
                        JsonObject personObject = Json.createObjectBuilder().
                                        add("resid", row[0].toString())
                                       .add("usage", round((double)row[1], 2))
                                       .add("temperature", row[2].toString())
                                       .add("usagedate", row[3].toString())
                                       .add("hours", row[4].toString()).build();
                        arrayBuilder.add(personObject);
                }
            }
        }
        else if(view.equalsIgnoreCase("daily")) {
            
            TypedQuery<Object[]> query = em.createQuery("SELECT u.resid.resid,sum(u.aircond + u.fridge + u.washmach) as usage,avg(u.temperature), u.usagedate FROM Usage u WHERE "
                    + "(FUNC('YEAR', :usagedate)*365 + FUNC('MONTH', :usagedate)*30 + FUNC('DAY', :usagedate)) - (FUNC('YEAR', u.usagedate)*365 + FUNC('MONTH', u.usagedate)*30 + FUNC('DAY', u.usagedate))  <= 30 AND "
                    + "(FUNC('YEAR', :usagedate)*365 + FUNC('MONTH', :usagedate)*30 + FUNC('DAY', :usagedate)) - (FUNC('YEAR', u.usagedate)*365 + FUNC('MONTH', u.usagedate)*30 + FUNC('DAY', u.usagedate))  >= 0 AND "
                    + "u.resid.resid = :resid Group by u.resid.resid, u.usagedate", Object[].class);
            date = new java.util.Date( df.parse( usagedate ).getTime() ); 
            query.setParameter("usagedate", date);
            query.setParameter("resid", resid);
            List<Object[]> queryResult = query.getResultList();
            for (Object[] row : queryResult)
            {
                    JsonObject personObject = Json.createObjectBuilder().
                                add("resid", row[0].toString())
                               .add("usage", round((double)row[1], 2))
                               .add("temperature", round((double)row[2], 2))
                               .add("usagedate",(row[3]).toString())
                               .build();
                    arrayBuilder.add(personObject);
            }
        }
        else {
            JsonObject personObject = Json.createObjectBuilder().
                            add("error", "You can only input hourly or daily for view variable!").build();
            arrayBuilder.add(personObject);        
        }     
        JsonArray jArray = arrayBuilder.build();     
        return jArray;
    } 
    
    
    //For android
    @POST
    @Path("postUsage")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUsage(String msg) {
        JSONObject ug = new JSONObject(msg);
        Double fridge = ug.getDouble("fridge");
        Double aircond = ug.getDouble("aircond");
        Double washmach = ug.getDouble("washmach");
        Double temperature = ug.getDouble("temperature");
        Integer rid = ug.getJSONObject("resid").getInt("resid");
        TypedQuery<Resident> q = em.createQuery("SELECT r FROM Resident r WHERE r.resid = :resid", Resident.class);
        q.setParameter("resid", rid);
        Resident resid = q.getResultList().get(0);
        
         
        String input = ug.getString("usagedate");
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );
            input = s0  + "GMT"+ s1;
        }
        
        Integer hours = ug.getInt("hours");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        java.util.Date usagedate = null;
        try { 
            usagedate = new java.util.Date( df.parse( input ).getTime() );
        } catch (ParseException ex) {
            Logger.getLogger(CredentialFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        Usage newUg = new Usage();
        newUg.setAircond(aircond);
        newUg.setFridge(fridge);
        newUg.setHours(hours);
        newUg.setResid(resid);
        newUg.setTemperature(temperature);
        newUg.setUsagedate(usagedate);
        newUg.setWashmach(washmach);
        em.persist(newUg);
        
        return Response.status(200).build();
    }
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
