package com.turkraft.springfilter.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.turkraft.springfilter.converter.StringCustomDateConverter.CustomDate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class StringCustomDateConverter implements Converter<String, CustomDate> {

    @Override
    public CustomDate convert(String source) {
        return new CustomDate(source);
    }

    public static class CustomDate implements Serializable {

        @JsonProperty("$date")
        private String value;

        public CustomDate(String id) {
            this.value = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
