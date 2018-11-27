package com.github.javydreamercsw.database.storage.db.controller.exceptions;

public class NonexistentEntityException extends Exception
{
  private static final long serialVersionUID = -3916760795141775553L;

  public NonexistentEntityException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public NonexistentEntityException(String message)
  {
    super(message);
  }
}
