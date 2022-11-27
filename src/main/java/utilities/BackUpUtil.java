package utilities;

import java.util.Arrays;
import java.util.Date;

public class BackUpUtil {
    /*private String fetchDataFromCache(Long einsiteId, String key, Date date) {
        String url = "http://15.206.30.242/InSite/redis/equipon/get/" + DateUtil.convertHyphonDate(date) + "-" + key + "-" + einsiteId + ".json";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers .add("Cookie", "app_ei=f7d93e6b-0063-4ddf-8f5b-92b88f5bcfe6");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, JSONObject.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        return responseEntity.getBody().toString();
    }*/
}