package org.example.tentrilliondollars.kakaopay;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class MakePayRequest {

    public PayRequest getReadyRequest(PayInfoDto payInfoDto){
        LinkedMultiValueMap<String,String> map=new LinkedMultiValueMap<>();

        map.add("cid","TC0ONETIME");
        map.add("partner_order_id","partner_order_id");
        map.add("partner_user_id","ten");
        map.add("item_name",payInfoDto.getItemName());
        map.add("quantity","1");
        map.add("total_amount",payInfoDto.getPrice()+"");
        map.add("tax_free_amount", "0");
        map.add("approval_url", "http://localhost:8080/payment/success"+"/"); // 성공 시 redirect url
        map.add("cancel_url", "http://localhost:8080/payment/cancel"); // 취소 시 redirect url
        map.add("fail_url", "http://localhost:8080/payment/fail"); // 실패 시 redirect url

        return new PayRequest("https://kapi.kakao.com/v1/payment/ready",map);
    }

    public PayRequest getApproveRequest(String tid,String pgToken){
        LinkedMultiValueMap<String,String> map=new LinkedMultiValueMap<>();


        map.add("cid", "TC0ONETIME");
        map.add("tid", tid);
        map.add("partner_order_id", "partner_order_id");
        map.add("partner_user_id", "ten");
        map.add("pg_token", pgToken);

        return new PayRequest("https://kapi.kakao.com/v1/payment/approve",map);
    }

    public CancelRequest getCancelRequest(String tid){
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("cid", "TC0ONETIME");
        map.add("tid", "T60cff4165886da991a7");
        map.add("cancel_amount", "29000");
        map.add("cancel_tax_free_amount", "0");
        map.add("cancel_vat_amount", "0");
        return new CancelRequest("https://kapi.kakao.com/v1/payment/cancel",map);
    }
}