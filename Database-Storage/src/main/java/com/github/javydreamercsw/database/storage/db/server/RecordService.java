package com.github.javydreamercsw.database.storage.db.server;

import java.util.List;

import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.controller.RecordJpaController;

public class RecordService extends Service<Record>
{
  private RecordJpaController rc
          = new RecordJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final RecordService INSTANCE = createRecordService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static RecordService createRecordService()
    {
      RecordService recordService = new RecordService();
      return recordService;
    }
  }

  /**
   * Declared private to ensure uniqueness of this Singleton.
   */
  private RecordService()
  {
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static RecordService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public void saveRecord(Record record) throws Exception
  {
    if (record.getRecordPK() == null)
    {
      rc.create(record);
    }
    else
    {
      rc.edit(record);
    }
  }
  
  @Override
  public List<Record> getAll()
  {
    return rc.findRecordEntities();
  }
}
