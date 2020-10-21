package models;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Accessors(fluent = true)
@Data
@RequiredArgsConstructor
public class Song {
    private final String title;
    private final String artist;
    private final String album;
    private final String fileLocation;
    private final int year;
}
