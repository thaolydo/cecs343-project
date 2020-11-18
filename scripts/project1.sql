CREATE TABLE Playlists (
    PlaylistName varchar(50) NOT NULL,
    CONSTRAINT Playlists_PK PRIMARY KEY(PlaylistName)
);

CREATE TABLE SongPlaylistAssociation (
    Location varchar(255) NOT NULL,
    PlaylistName varchar(50) NOT NULL,
    CONSTRAINT SongPlaylistAssociation_PK PRIMARY KEY (Location, PlaylistName),
    CONSTRAINT Location_Songs_FK FOREIGN KEY (Location) REFERENCES Songs(Location) ON DELETE CASCADE,
    CONSTRAINT PlaylistName_Playlists_FK FOREIGN KEY (PlaylistName) REFERENCES Playlists(PlaylistName) ON DELETE CASCADE
);
