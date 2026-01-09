package wade.owen.watts.base_jetpack.data.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val title: String,
    @ColumnInfo val content: String,
    @ColumnInfo(name = "created_date") val createdDate: Int,
    @ColumnInfo(name = "updated_date") val updatedDate: Int,
)
