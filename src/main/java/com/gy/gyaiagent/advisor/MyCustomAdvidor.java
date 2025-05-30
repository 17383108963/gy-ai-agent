package com.gy.gyaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyCustomAdvidor implements CallAroundAdvisor, StreamAroundAdvisor {


	@Override
	public String getName() { 
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() { 
		return 0;
	}

	@Override
	public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

		advisedRequest = this.before(advisedRequest);

		AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

		this.observeAfter(advisedResponse);

		return advisedResponse;
	}

	private void observeAfter(AdvisedResponse advisedResponse) {
		log.info("AI Response: {}", advisedResponse.response().getResult().getOutput().getText());
	}

	private AdvisedRequest before(AdvisedRequest advisedRequest) {
		log.info("User Request: {}", advisedRequest.userText());
		return advisedRequest;
	}

	@Override
	public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

		advisedRequest = this.before(advisedRequest);

		Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);

        return new MessageAggregator().aggregateAdvisedResponse(advisedResponses, this::observeAfter); 
	}
}