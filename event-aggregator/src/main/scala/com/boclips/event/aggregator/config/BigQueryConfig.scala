package com.boclips.event.aggregator.config

import com.boclips.event.aggregator.infrastructure.bigquery.TempFolderName

case class BigQueryConfig(
                         serviceAccountKeyFile: String,
                         projectId: String,
                         dataset: String,
                         bucket: String,
                         tmpDirName: String,
                         )

object BigQueryConfig {
  def apply(): BigQueryConfig = {
    BigQueryConfig(
      serviceAccountKeyFile = Env("BIG_QUERY_SERVICE_ACCOUNT_KEY"),
      projectId = Env("BIG_QUERY_PROJECT_ID"),
      dataset = Env("BIG_QUERY_DATASET"),
      bucket = Env("BIG_QUERY_LOAD_VIA_BUCKET"),
      tmpDirName = TempFolderName(),
    )
  }
}
