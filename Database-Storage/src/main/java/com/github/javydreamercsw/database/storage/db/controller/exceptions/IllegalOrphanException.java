package com.github.javydreamercsw.database.storage.db.controller.exceptions;

import java.util.ArrayList;
import java.util.List;

public class IllegalOrphanException extends Exception
{
  private static final long serialVersionUID = -5133597178133667358L;
  private List<String> messages;

  public IllegalOrphanException(List<String> messages)
  {
    super((messages != null && messages.size() > 0 ? messages.get(0) : null));
    if (messages == null)
    {
      this.messages = new ArrayList<>();
    }
    else
    {
      this.messages = messages;
    }
  }

  public List<String> getMessages()
  {
    return messages;
  }
}
