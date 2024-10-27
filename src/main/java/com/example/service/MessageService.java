package com.example.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

        public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer id) {
          return messageRepository.findById(id);
       // return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
    }

    public Message updateMessage(Message updatedMessage, Integer id) {
        return messageRepository.findById(id).map(message -> {
            message.setMessageText(updatedMessage.getMessageText());
            message.setTimePostedEpoch(updatedMessage.getTimePostedEpoch());
            return messageRepository.save(message);
        }).orElseThrow(() -> new RuntimeException("Message not found with id " + id));
    }

    public String deleteMessage(Integer id) {
        messageRepository.deleteById(id);
        return "Message deleted with id: " + id;
    }
}
