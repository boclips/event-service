package com.boclips.event.infrastructure;

public class EventFields {

    public static class Type {
        public final static String RESOURCES_SEARCHED = "RESOURCES_SEARCHED";
        public final static String VIDEOS_SEARCHED = "VIDEOS_SEARCHED";
        public final static String VIDEO_SEGMENT_PLAYED = "VIDEO_SEGMENT_PLAYED";
        public final static String VIDEO_PLAYER_INTERACTED_WITH = "VIDEO_PLAYER_INTERACTED_WITH";
        public final static String VIDEO_ADDED_TO_COLLECTION = "VIDEO_ADDED_TO_COLLECTION";
        public final static String VIDEO_REMOVED_FROM_COLLECTION = "VIDEO_REMOVED_FROM_COLLECTION";
        public final static String VIDEO_INTERACTED_WITH = "VIDEO_INTERACTED_WITH";
        public final static String COLLECTION_BOOKMARK_CHANGED = "COLLECTION_BOOKMARK_CHANGED";
        public final static String COLLECTION_VISIBILITY_CHANGED = "COLLECTION_VISIBILITY_CHANGED";
        public final static String COLLECTION_SUBJECTS_CHANGED = "COLLECTION_SUBJECTS_CHANGED";
        public final static String COLLECTION_AGE_RANGE_CHANGED = "COLLECTION_AGE_RANGE_CHANGED";
        public final static String COLLECTION_INTERACTED_WITH = "COLLECTION_INTERACTED_WITH";
        public final static String USER_EXPIRED = "USER_EXPIRED";
        public final static String PLATFORM_INTERACTED_WITH = "PLATFORM_INTERACTED_WITH";
        public final static String PAGE_RENDERED = "PAGE_RENDERED";
        public final static String SEARCH_QUERY_COMPLETIONS_SUGGESTED = "SEARCH_QUERY_COMPLETIONS_SUGGESTED";
    }

    public final static String TYPE = "type";
    public final static String SUBTYPE = "subtype";
    public final static String PAYLOAD = "payload";

    public final static String TIMESTAMP = "timestamp";

    public final static String VIDEO_ID = "videoId";
    public final static String COLLECTION_ID = "collectionId";

    public final static String USER_ID = "userId";
    public final static String EXTERNAL_USER_ID = "externalUserId";
    public final static String DEVICE_ID = "deviceId";

    public final static String ORGANISATION_ID = "organisationId";
    public final static String ORGANISATION_TYPE = "organisationType";
    public final static String ORGANISATION_PARENT_ID = "organisationParentId";
    public final static String ORGANISATION_PARENT_TYPE = "organisationParentType";

    public final static String URL = "url";

    public final static String VIEWPORT_WIDTH = "viewportWidth";
    public final static String VIEWPORT_HEIGHT = "viewportHeight";
    public final static String IS_RESIZE = "isResize";

    public final static String PLAYBACK_VIDEO_INDEX = "videoIndex";
    public final static String PLAYBACK_SEGMENT_START_SECONDS = "segmentStartSeconds";
    public final static String PLAYBACK_SEGMENT_END_SECONDS = "segmentEndSeconds";

    public final static String PLAYER_INTERACTED_WITH_CURRENT_TIME = "currentTime";

    public final static String SEARCH_QUERY = "query";
    public final static String SEARCH_RESOURCE_TYPE = "resourceType";
    public final static String SEARCH_RESULTS_PAGE_INDEX = "pageIndex";
    public final static String SEARCH_RESULTS_PAGE_SIZE = "pageSize";
    public final static String SEARCH_RESULTS_PAGE_RESOURCE_IDS = "pageResourceIds";
    public final static String SEARCH_RESULTS_PAGE_VIDEO_IDS = "pageVideoIds";
    public final static String SEARCH_RESULTS_TOTAL = "totalResults";
    public final static String SEARCH_QUERY_PARAMS = "queryParams";

    public final static String COLLECTION_BOOKMARK_CHANGED_IS_BOOKMARKED = "isBookmarked";

    public final static String COLLECTION_VISIBILITY_CHANGED_IS_DISCOVERABLE = "isDiscoverable";

    public final static String COLLECTION_SUBJECTS_CHANGED_SUBJECTS = "subjects";

    public final static String COLLECTION_AGE_RANGE_CHANGED_RANGE_MIN = "rangeMin";
    public final static String COLLECTION_AGE_RANGE_CHANGED_RANGE_MAX = "rangeMax";

    public final static String COMPLETION_ID = "completionId";
    public final static String IMPRESSIONS = "impressions";
    public final static String COMPONENT_ID = "componentId";
}
