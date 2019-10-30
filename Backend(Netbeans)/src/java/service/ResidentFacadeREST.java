/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.validator.Validator;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
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
import restws.Credential;
import restws.Resident;

/**
 *
 * @author kasal
 */
@Stateless
@Path("restws.resident")
public class ResidentFacadeREST extends AbstractFacade<Resident> {

    @PersistenceContext(unitName = "AssignmentPU")
    private EntityManager em;
    
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    

    public ResidentFacadeREST() {
        super(Resident.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Resident entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Resident entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Resident find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    //Static query by resident ID
    @GET
    @Path("findByResid/{resid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByResid(@PathParam("resid") Integer resid) {
        Query query = em.createNamedQuery("Resident.findByResid");
        query.setParameter("resid", resid);
        return query.getResultList();
    }
    
//    @Path("postByResid/{id}")
//    @POST
//    @Consumes("text/plain")
//    @Produces("application/json")
//    public String postByUnPw(@PathParam("id") String username, @PathParam("passwdHash") String passwdHash)
//    {
//        JSONObject jo = new JSONObject();
//        jo.put("id", nim);
//        jo.put("nama", "Budi");
//        jo.put("message", data);
//        return(jo.toJSONString());
//    }
//    
    //Static query by resident's first name
    @GET
    @Path("findByFname/{fname}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByFname(@PathParam("fname") String fname) {
        Query query = em.createNamedQuery("Resident.findByFname");
        query.setParameter("fname", fname);
        return query.getResultList();
    }
    
    //Static query by resident's surname
    @GET
    @Path("findBySname/{sname}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findBySname(@PathParam("sname") String sname) {
        Query query = em.createNamedQuery("Resident.findBySname");
        query.setParameter("sname", sname);
        return query.getResultList();
    }
    
    //Static query by resident's date of birth
    //Data should in form like yyyy-MM-dd
    @GET
    @Path("findByDob/{dob}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByDob(@PathParam("dob") String dob) throws ParseException {
        Query query = em.createNamedQuery("Resident.findByDob");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date;
        date = new java.util.Date( df.parse( dob ).getTime() ); 
        query.setParameter("dob", date);
        return query.getResultList();
    }
    
    //Static query by resident's address
    @GET
    @Path("findByAddress/{address}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByAddress(@PathParam("address") String address) {
        Query query = em.createNamedQuery("Resident.findByAddress");
        query.setParameter("address", address);
        return query.getResultList();
    }
    
    //Static query by resident's postcode
    @GET
    @Path("findByPostcode/{postcode}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByPostcode(@PathParam("postcode") String postcode) {
        Query query = em.createNamedQuery("Resident.findByPostcode");
        query.setParameter("postcode", postcode);
        return query.getResultList();
    }
    
    //Static query by resident's email
    @GET
    @Path("findByEmail/{email}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByFfindByEmailname(@PathParam("email") String email) {
        Query query = em.createNamedQuery("Resident.findByEmail");
        query.setParameter("email", email);
        return query.getResultList();
    }
    
    //Static query by resident's mobile phone number
    @GET
    @Path("findByMobile/{mobile}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByMobile(@PathParam("mobile") String mobile) {
        Query query = em.createNamedQuery("Resident.findByMobile");
        query.setParameter("mobile", mobile);
        return query.getResultList();
    }
    
    //Static query by resident number in a house
    @GET
    @Path("findByNumber/{number}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByNumber(@PathParam("number") int number) {
        Query query = em.createNamedQuery("Resident.findByNumber");
        query.setParameter("number", number);
        return query.getResultList();
    }
    
    //Static query by resident's energy provider name
    @GET
    @Path("findByProviderName/{providerName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByProviderName(@PathParam("providerName") String providerName) {
        Query query = em.createNamedQuery("Resident.findByProviderName");
        query.setParameter("providerName", providerName);
        return query.getResultList();
    }
    
    //Dynamic query by resident's first name and surname
    @GET
    @Path("findByFnameSname/{fname}/{sname}")
    @Produces({"application/json"})
    public List<Resident> findByFnameSname(@PathParam("fname") String fname, @PathParam("sname") String sname) {
        TypedQuery<Resident> q = em.createQuery("SELECT r FROM Resident r WHERE UPPER(r.fname) = UPPER(:fname) "
                + "AND UPPER(r.sname) = UPPER(:sname)", Resident.class);
        q.setParameter("fname", fname);
        q.setParameter("sname", sname);
        return q.getResultList();
    } 
    
    //For android
    @POST
    @Path("postResident")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postResident(String msg) {
        JSONObject rd = new JSONObject(msg);
        String fname = rd.getString("fname");
       
        String sname = rd.getString("sname");
        String address = rd.getString("address");
        String postcode = rd.getString("postcode");
        String email = rd.getString("email");
        String mobile = rd.getString("mobile");
        Integer number = rd.getInt("number");
        String providerName = rd.getString("providerName");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        
        String input = rd.getString("dob");
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );
            input = s0  + "GMT"+ s1;
        }

        java.util.Date dob = null;
        try { 
            dob = new java.util.Date( df.parse( input ).getTime() );
        } catch (ParseException ex) {
            Logger.getLogger(CredentialFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        Resident newRd = new Resident();
        newRd.setAddress(address);
        newRd.setDob(dob);
        newRd.setEmail(email);
        newRd.setFname(fname);
        newRd.setMobile(mobile);
        newRd.setNumber(number);
        newRd.setPostcode(postcode);
        newRd.setProviderName(providerName);
        newRd.setSname(sname);
        em.persist(newRd);
        
        TypedQuery<Integer> q = em.createQuery("SELECT max(r.resid) FROM Resident r", Integer.class);
        newRd.setResid(q.getSingleResult());
        return Response.status(200).entity(newRd).build();
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
