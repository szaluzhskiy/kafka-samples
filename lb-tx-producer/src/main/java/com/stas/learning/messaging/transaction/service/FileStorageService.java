package com.stas.learning.messaging.transaction.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.stas.learning.messaging.transaction.config.FileStorageConfig.FileStorageProperties;
import com.stas.learning.messaging.transaction.exception.FileStorageException;
import java.io.File;
import java.io.FileInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

  private final AmazonS3 s3Client;
  private final FileStorageProperties fileStorageProperties;

  public String put(String filename, File file) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.length());
    try {
      final PutObjectRequest putObjectRequest = new PutObjectRequest(
          fileStorageProperties.getNamespace(),
          filename,
          new FileInputStream(file),
          objectMetadata);
      final PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);
      log.info("File was stored: {} with ETag = {}", filename, putObjectResult.getETag());
      return filename;
    } catch (Exception e) {
      if (e instanceof AmazonServiceException && ((AmazonServiceException) e).getStatusCode() == 409) {
        // TODO: we can do something with conflict
        log.error("File {} is already exist", filename, e);
      }
      throw new FileStorageException(
          String.format("Error putting file to repository - bucket ''%s'', file name ''%s''",
              fileStorageProperties.getNamespace(), filename),
          e);
    }
  }

  public void delete(String filename) {
    try {
      s3Client.deleteObject(fileStorageProperties.getNamespace(), filename);
    } catch (Exception e) {
      throw new FileStorageException(
          String.format("Error deleting file from repository - bucket ''%s'', file name ''%s''",
              fileStorageProperties.getNamespace(), filename),
          e);
    }
  }
}
