package models;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Accessors(fluent = true)
@Data
@RequiredArgsConstructor
public class SongPlaylistAssociation {
    private final String songLocation;
    private final String playlistID;
}