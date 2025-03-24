package com.example.MyBlog.domain.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CustomByteArrayRedisSerializer implements RedisSerializer<byte[]> {
    @Override
    public byte[] serialize(byte[] value) throws SerializationException {
        return value;
    }

    @Override
    public byte[] deserialize(byte[] bytes) throws SerializationException {
        return bytes;
    }
}
