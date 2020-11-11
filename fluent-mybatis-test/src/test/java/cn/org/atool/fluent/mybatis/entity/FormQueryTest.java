package cn.org.atool.fluent.mybatis.entity;

import cn.org.atool.fluent.mybatis.generate.Refs;
import cn.org.atool.fluent.mybatis.generate.entity.StudentEntity;
import cn.org.atool.fluent.mybatis.model.FormQuery;
import cn.org.atool.fluent.mybatis.model.StdPagedList;
import cn.org.atool.fluent.mybatis.model.TagPagedList;
import cn.org.atool.fluent.mybatis.test.BaseTest;
import org.junit.jupiter.api.Test;

public class FormQueryTest extends BaseTest {
    @Test
    public void testStdPaged() {
        StdPagedList<StudentEntity> paged = new FormQuery()
            .add.eq(Refs.Column.Student.userName, "xx")
            .add.between(Refs.Column.Student.age, 12, 40)
            .setCurrPage(0)
            .paged(StudentEntity.class);
        db.sqlList().wantFirstSql().end("WHERE is_deleted = ? " +
            "AND env = ? " +
            "AND user_name = ? " +
            "AND age BETWEEN ? AND ?");
        db.sqlList().wantSql(1).end("WHERE is_deleted = ? " +
            "AND env = ? " +
            "AND user_name = ? " +
            "AND age BETWEEN ? AND ? " +
            "LIMIT ?, ?");
    }

    @Test
    public void testTagPaged() {
        TagPagedList<StudentEntity> paged = new FormQuery()
            .add.eq(Refs.Column.Student.userName, "xx")
            .add.between(Refs.Column.Student.age, 12, 40)
            .setNextId("0")
            .paged(StudentEntity.class);
        db.sqlList().wantFirstSql().end("WHERE is_deleted = ? " +
            "AND env = ? " +
            "AND user_name = ? " +
            "AND age BETWEEN ? AND ? " +
            "AND id >= ? " +
            "LIMIT ?, ?");
    }
}
