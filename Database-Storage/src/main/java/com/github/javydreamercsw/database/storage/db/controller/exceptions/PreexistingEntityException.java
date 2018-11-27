package com.github.javydreamercsw.database.storage.db.controller.exceptions;

public class PreexistingEntityException extends Exception
{
  private static final long serialVersionUID = 2897972290405471195L;

  public PreexistingEntityException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public PreexistingEntityException(String message)
  {
    super(message);
  }
}
