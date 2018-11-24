package com.github.javydreamercsw.database.storage.db.server;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;

import com.github.javydreamercsw.database.storage.db.controller.FormatJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class FormatServer extends Format implements DatabaseEntity<Format>
{
  private static final long serialVersionUID = 1121455691184116787L;

  public FormatServer()
  {
  }

  public FormatServer(String name)
  {
    super(name);
  }

  @Override
  public int write2DB()
  {
    FormatJpaController controller
            = new FormatJpaController(DataBaseManager.getEntityManagerFactory());
    if (getId() != null)
    {
      Format r = controller.findFormat(getId());
      update(r, this);
      try
      {
        controller.edit(r);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      Format r = new Format();
      update(r, this);
      try
      {
        controller.create(r);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
      setId(r.getId());
    }
    return getId();
  }

  @Override
  public void update(Format target, Format source)
  {
    target.setDescription(source.getDescription());
    target.setMatchEntryList(source.getMatchEntryList());
    target.setName(source.getName());
  }

  @Override
  public Format getEntity()
  {
    return new FormatJpaController(DataBaseManager.getEntityManagerFactory())
            .findFormat(getId());
  }
}
