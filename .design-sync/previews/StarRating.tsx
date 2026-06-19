import React from 'react';
import { StarRating } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', flexDirection: 'column', gap: 18, alignItems: 'flex-start' }}>
    {children}
  </div>
);

export const Scores = () => (
  <Stage>
    <StarRating value={1} />
    <StarRating value={2} />
    <StarRating value={3} />
  </Stage>
);

export const Large = () => (
  <div style={{ background: '#0e2a47', padding: 32, textAlign: 'center' }}>
    <StarRating value={3} size={52} />
  </div>
);

export const FiveStar = () => (
  <Stage>
    <StarRating value={4} max={5} size={30} />
  </Stage>
);
