package com.test.kafka.transaction.controller;


import com.test.kafka.transaction.domain.TxDataChild;
import com.test.kafka.transaction.domain.TxDataKey;
import com.test.kafka.transaction.service.TxProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("tx")
@RequiredArgsConstructor
public class TestController {

  private final TxProducerService transactionProducerService;

  @PostMapping
  public void sendMessages() {
    TxDataKey txDataKey = new TxDataKey("1", "first name");
    TxDataChild txDataChild = new TxDataChild("parent value 1", "child value 1");
    transactionProducerService.sendInTransaction(txDataKey, txDataChild);
  }

  @PostMapping("/annotation")
  public void sendMessagesWithTransactionalAnnotation() {
    TxDataKey txDataKey = new TxDataKey("2", "annotation name");
    TxDataChild txDataChild = new TxDataChild("parent value annotation", "child value annotation");
    transactionProducerService.sendWithAnnotation(txDataKey, txDataChild);
  }

  @PostMapping("/error")
  public void sendErrorMessage() {
    TxDataKey txDataKeyError = new TxDataKey("error", "error name");
    TxDataChild txDataChildError = new TxDataChild("parent value error", "child value error");
    transactionProducerService.sendInTransactionWithError(txDataKeyError, txDataChildError);
  }
}
