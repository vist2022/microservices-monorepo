package telran.daily_farm.payment.paypal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.daily_farm.order.entity.OrderFarmSet;
import telran.daily_farm.payment.entity.Payment;
import telran.daily_farm.payment.repo.PaymentRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    private final RestTemplate restTemplate;
    private final PayPalAuthService authService;
    private final PaymentRepository paymentRepo;
    private final ObjectMapper objectMapper;

    @Value("${paypal.api-url}")
    private String apiUrl;

    @Value("${daily.farm.domain}")
    private String domain;
    
    public String getPaypalLink(Double amount , UUID orderId) {
        String accessToken = authService.getAccessToken();
        log.info("PayPalService : got access token for farmer");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String orderRequestJson = """
                {
                  "intent": "CAPTURE",
                  "purchase_units": [{
                    "amount": {
                      "currency_code": "USD",
                      "value": "%s"
                    }
                  }],
                  "application_context": {
                    "return_url": "%s/paypal/success",
                    "cancel_url": "%s/paypal/cancel"
                  }
                }
                """.formatted(amount, domain, domain);

        HttpEntity<String> request = new HttpEntity<>(orderRequestJson, headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl + "/v2/checkout/orders",
                HttpMethod.POST, request, Map.class);
        
        log.info("PayPalService : Got response from paypal");
        if (response.getStatusCode() == HttpStatus.CREATED) {
        	 log.info("PayPalService : response status CREATED");
            Map<String, Object> responseBody = response.getBody();
            
            String paypalOrderId = (String) responseBody.get("id");
            log.info("PayPalService : paypalOrderId - {}", paypalOrderId);
            
            
            Payment payment = Payment.builder()
            				.amount(amount)
            				.createdAt(LocalDateTime.now())
            				.orderFarmSet(new OrderFarmSet(orderId))
            				.paymentProviderId(paypalOrderId)
            				.provider("PayPal")
            				.refunded(false)
            				.status("PENDING")
            				.build();
            		
            paymentRepo.save(payment);
            log.info("PayPalService : payment creatred and saved to database");
            
            List<Map<String, String>> links = (List<Map<String, String>>) responseBody.get("links");
            return links.stream()
                .filter(link -> "approve".equals(link.get("rel")))
                .findFirst()
                .map(link -> link.get("href"))
                .orElseThrow(() -> new RuntimeException("Link not found"));
        }
        throw new RuntimeException("Error creating payment PayPal");
    }
    
    public boolean isPaid(String orderId) {
        String accessToken = authService.getAccessToken(); 
        log.info("PayPalService : isPaid - got access token for farmer");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
            "https://api.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture",
            HttpMethod.POST, request, Map.class);

        Payment payment = paymentRepo.findByPaymentProviderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        log.info("PayPalService : isPaid - payment exists in database");
        
        String status = (String) response.getBody().get("status");
        payment.setStatus(status);
        paymentRepo.save(payment);
        log.info("PayPalService : isPaid - payment status updated to {}", status);
        
        return  "COMPLETED".equals(status);
 
    }
    
    public void cancelPayPalOrder(String paypalOrderId) {
    	
    	 String status = getPaymentOrderStatus(paypalOrderId);
         if (!"CREATED".equals(status)) {
             System.out.println("Заказ не может быть отменен, текущий статус: " + status);
             return ;
         }
    	
    	String accessToken = authService.getAccessToken(); 
        String url = "https://api.sandbox.paypal.com/v2/checkout/orders/" + paypalOrderId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); 
        headers.setContentType(MediaType.APPLICATION_JSON);
       
//        String patchRequest = "[{\"op\": \"replace\", \"path\": \"/status\", \"value\": \"CANCELLED\"}]";
        String requestBody = "[{\"op\":\"replace\",\"path\":\"/status\",\"value\":\"VOIDED\"}]";    
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        
        restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
        log.info("PayPalService: cancelPayPalOrder - order canceled.");
    }
    
    public String getPaymentOrderStatus(String paypalOrderId) {
        String url = "https://api.sandbox.paypal.com/v2/checkout/orders/" + paypalOrderId;
        HttpHeaders headers = new HttpHeaders();
        String accessToken = authService.getAccessToken(); 
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

         
	
		try {
			JsonNode	jsonNode = objectMapper.readTree(response.getBody());
			    String status = jsonNode.get("status").asText();
			    
			    log.info("PayPalService: getOrderStatus - {}", status);
			    return status; // "CREATED", "APPROVED", "COMPLETED" 
		} catch (JsonProcessingException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error. Can't get payment status");
		}


    
    }
}
