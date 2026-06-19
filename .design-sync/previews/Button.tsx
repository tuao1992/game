import React from 'react';
import { Button } from '@weldrite/design-system';

const Stage = ({ children, col = false }: { children: React.ReactNode; col?: boolean }) => (
  <div
    style={{
      background: '#0e2a47',
      padding: 28,
      display: 'flex',
      flexDirection: col ? 'column' : 'row',
      gap: 16,
      alignItems: col ? 'stretch' : 'center',
      flexWrap: 'wrap',
    }}
  >
    {children}
  </div>
);

export const Variants = () => (
  <Stage>
    <Button variant="primary">PLAY</Button>
    <Button variant="secondary">Career</Button>
    <Button variant="success">Confirm</Button>
    <Button variant="danger">Exit</Button>
    <Button variant="ghost">Settings</Button>
  </Stage>
);

export const Sizes = () => (
  <Stage>
    <Button size="sm" variant="primary">
      Small
    </Button>
    <Button size="md" variant="primary">
      Medium
    </Button>
    <Button size="lg" variant="primary">
      Large
    </Button>
  </Stage>
);

export const States = () => (
  <Stage>
    <Button variant="primary">Enabled</Button>
    <Button variant="primary" disabled>
      Disabled
    </Button>
  </Stage>
);

export const FullWidth = () => (
  <Stage col>
    <Button variant="primary" size="lg" fullWidth>
      START CAREER
    </Button>
    <Button variant="ghost" fullWidth>
      Restore Progress
    </Button>
  </Stage>
);
