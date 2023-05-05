package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay


        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();

        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        SubscriptionType substype = subscription.getSubscriptionType();
        int amount = 0;

        if(substype == SubscriptionType.BASIC){
            amount = 500 + 200 * subscription.getNoOfScreensSubscribed();
        }
        else if (substype == SubscriptionType.PRO) {
            amount = 800 + 250 * subscription.getNoOfScreensSubscribed();
        }
        else {
            amount = 1000 + 350 * subscription.getNoOfScreensSubscribed();
        }

        subscription.setTotalAmountPaid(amount);
        subscription.setUser(user);

        subscriptionRepository.save(subscription);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Subscription subscription = subscriptionRepository.findById(userId).get();
        SubscriptionType subsType = subscription.getSubscriptionType();

        int toPay = 0;
        if(subsType == SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }

       else if(subsType == SubscriptionType.BASIC){
           subscription.setSubscriptionType(SubscriptionType.PRO);
           toPay =  800 + 250 * subscription.getNoOfScreensSubscribed() - subscription.getTotalAmountPaid();
       }

       else if(subsType == SubscriptionType.PRO){
           subscription.setSubscriptionType(SubscriptionType.ELITE);
           toPay = 1000 + 350 * subscription.getNoOfScreensSubscribed() - subscription.getTotalAmountPaid();
       }

       subscriptionRepository.save(subscription);

        return toPay;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalRevenue = 0;

        for(Subscription s : subscriptionList){
            totalRevenue += s.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
