package sonia.com.flohtweets.model

data class TwitterAPIResponse(var statuses: List<Statuses>, var search_metadata: SearchMetadata)