package boss.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.primitives.Floats;

import boss.common.OpenAI;
import boss.common.Pinecone;
import boss.model.Product;
import boss.service.MasterProductService;

@Controller
public class ChatbotController {

	@Autowired
	MasterProductService productService;

	@RequestMapping("requestChatbot.do")
	public @ResponseBody Map<String, Object> requestChatbot(String prompt) {
		Map<String, Object> map = new HashMap<String, Object>();

		OpenAI openAI = OpenAI.getInstance();
		String model = "gpt-4-1106-preview";
		Pinecone vecStore = Pinecone.getInstance();
		List<Double> embedding;
		int pid;
		
		if(openAI.getService() != null) {
			embedding = openAI.getEmbedding(prompt);
			pid = vecStore.query(Floats.toArray(embedding), 1);
		} else {
			pid = -1;
		}
		
		String answer;
		if(pid > 0) {
			Product product = productService.selectProductOne(pid);
			map.put("product", product);
			answer = "이런 옷은 어떠세요?";
		} else {
			map.put("product", "none");
//			String answer = openAI.getRespByTxt(prompt, model);
			answer = "죄송합니다. 원하시는 상품이 없습니다.";
		}
		map.put("answer", answer);

		return map;
	}
}
