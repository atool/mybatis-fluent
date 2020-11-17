package cn.org.atool.fluent.mybatis.generate;

import cn.org.atool.fluent.mybatis.generate.dm.*;
import cn.org.atool.fluent.mybatis.generate.mix.*;
import org.test4j.module.database.IDataSourceScript;
import org.test4j.module.spec.annotations.Mix;

import java.util.List;

/**
 * ATM: Application Table Manager
 *
 * @author Powered By Test4J
 */
public interface ATM {
  /**
   * 应用表名
   */
  interface Table {
    String noAutoId = "no_auto_id";

    String noPrimary = "no_primary";

    String studentScore = "student_score";

    String homeAddress = "home_address";

    String student = "student";

    String memberFavorite = "t_member_favorite";

    String memberLove = "t_member_love";

    String member = "t_member";
  }

  /**
   * table or entity data构造器
   */
  interface DataMap {
    NoAutoIdDataMap.Factory noAutoId = new NoAutoIdDataMap.Factory();

    NoPrimaryDataMap.Factory noPrimary = new NoPrimaryDataMap.Factory();

    StudentScoreDataMap.Factory studentScore = new StudentScoreDataMap.Factory();

    HomeAddressDataMap.Factory homeAddress = new HomeAddressDataMap.Factory();

    StudentDataMap.Factory student = new StudentDataMap.Factory();

    MemberFavoriteDataMap.Factory memberFavorite = new MemberFavoriteDataMap.Factory();

    MemberLoveDataMap.Factory memberLove = new MemberLoveDataMap.Factory();

    MemberDataMap.Factory member = new MemberDataMap.Factory();
  }

  /**
   * 应用表数据操作
   */
  @org.test4j.module.spec.annotations.Mixes
  class Mixes {
    @Mix
    public NoAutoIdTableMix noAutoIdTableMix;

    @Mix
    public NoPrimaryTableMix noPrimaryTableMix;

    @Mix
    public StudentScoreTableMix studentScoreTableMix;

    @Mix
    public HomeAddressTableMix homeAddressTableMix;

    @Mix
    public StudentTableMix studentTableMix;

    @Mix
    public MemberFavoriteTableMix memberFavoriteTableMix;

    @Mix
    public MemberLoveTableMix memberLoveTableMix;

    @Mix
    public MemberTableMix memberTableMix;

    public void cleanAllTable() {
      this.noAutoIdTableMix.cleanNoAutoIdTable();
      this.noPrimaryTableMix.cleanNoPrimaryTable();
      this.studentScoreTableMix.cleanStudentScoreTable();
      this.homeAddressTableMix.cleanHomeAddressTable();
      this.studentTableMix.cleanStudentTable();
      this.memberFavoriteTableMix.cleanMemberFavoriteTable();
      this.memberLoveTableMix.cleanMemberLoveTable();
      this.memberTableMix.cleanMemberTable();
    }
  }

  /**
   * 应用数据库创建脚本构造
   */
  class Script implements IDataSourceScript {
    @Override
    public List<Class> getTableKlass() {
      return list(
      	NoAutoIdDataMap.class,
      	NoPrimaryDataMap.class,
      	StudentScoreDataMap.class,
      	HomeAddressDataMap.class,
      	StudentDataMap.class,
      	MemberFavoriteDataMap.class,
      	MemberLoveDataMap.class,
      	MemberDataMap.class
      );
    }

    @Override
    public IndexList getIndexList() {
      return new IndexList();
    }
  }
}