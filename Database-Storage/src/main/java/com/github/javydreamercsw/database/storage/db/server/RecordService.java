package com.github.javydreamercsw.database.storage.db.server;

import java.util.List;

import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.controller.RecordJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TeamHasFormatRecordJpaController;

public class RecordService extends Service<Record>
{
  private RecordJpaController rc
          = new RecordJpaController(DataBaseManager.getEntityManagerFactory());
  private TeamHasFormatRecordJpaController thfrc
          = new TeamHasFormatRecordJpaController(DataBaseManager.getEntityManagerFactory());

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

  /**
   * Save a tournament record.
   *
   * @param record Record to save.
   * @throws Exception Persisting to database.
   */
  public void saveRecord(Record record) throws Exception
  {
    if (record.getId() == null)
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

  /**
   * Save overall record.
   *
   * @param teamRecord Record to save.
   * @throws Exception Persisting to database.
   */
  public void saveRecord(TeamHasFormatRecord teamRecord) throws Exception
  {
    if (teamRecord.getTeamHasFormatRecordPK() == null)
    {
      thfrc.create(teamRecord);
    }
    else
    {
      thfrc.edit(teamRecord);
    }
  }
}
