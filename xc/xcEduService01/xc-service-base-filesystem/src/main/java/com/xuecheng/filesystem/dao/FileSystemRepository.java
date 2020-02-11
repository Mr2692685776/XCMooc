package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/11 8:41
 */
@Repository
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
