package com.kingston.mmorpg.framework.net.socket.serializer;

import java.lang.reflect.Field;
import java.util.List;

import io.netty.buffer.ByteBuf;

/**
 * 消息或vo的解析器
 * @author kingston
 */
public class MessageSerializer extends Serializer {

	private List<FieldCodecMeta> fieldsMeta;

	public static MessageSerializer valueOf(List<FieldCodecMeta> fieldsMeta) {
		MessageSerializer serializer = new MessageSerializer();
		serializer.fieldsMeta = fieldsMeta;
		return serializer;
	}

	@Override
	public Object decode(ByteBuf in, Class<?> type, Class<?> wrapper) {
		try {
			Object bean = type.newInstance();
			for (FieldCodecMeta fieldMeta:fieldsMeta) {
				Field field = fieldMeta.getField();
				Serializer fieldCodec = fieldMeta.getSerializer();
				Object value = fieldCodec.decode(in, fieldMeta.getType(), fieldMeta.getWrapper());
				field.set(bean, value);
			}
			return bean;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void encode(ByteBuf out, Object message, Class<?> wrapper) {
		try {
			for (FieldCodecMeta fieldMeta:fieldsMeta) {
				Field field = fieldMeta.getField();
				Serializer fieldCodec = Serializer.getSerializer(fieldMeta.getType());
				Object value = field.get(message) ;
				fieldCodec.encode(out, value, fieldMeta.getWrapper());
			}
		}catch(Exception e) {

		}
	}

}
