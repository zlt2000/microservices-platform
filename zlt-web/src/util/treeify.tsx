type Configure = {
  id: string;
  parentId: string;
};

type Tree<T> = T & {
  children?: Tree<T>[];
};

export function treeify<T>(data: T[], configure: Partial<Configure>): Tree<T>[] {
  const config: Configure = Object.assign(
    {
      id: 'id',
      parentId: 'parentId',
    },
    configure,
  );

  const disposable = {}; // an object which is temp used.

  const roots: Tree<T>[] = []; // Root node
  const setRoots = (el: Tree<T>) => {
    roots.push(el);
  };

  for (let i = 0; i < data.length; i++) {
    const el = data[i];
    const id = el[config.id];
    const parentId = el[config.parentId];

    if (!(id in disposable)) {
      disposable[id] = [];
    }

    if (!(parentId in disposable)) {
      disposable[parentId] = [];
    }

    el.children = disposable[id];
    disposable[parentId].push(el);
    if (parentId == -1) setRoots(el);
  }

  return roots;
}
