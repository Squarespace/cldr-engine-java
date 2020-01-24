package com.squarespace.cldrengine.api;

import java.util.List;

public interface MessageFormatFunc {

  String apply(List<Object> args, List<String> options);

}
